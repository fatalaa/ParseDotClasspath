package hu.infostyle.parsedotclasspath;

import hu.infostyle.parsedotclasspath.buildtemplate.AndroidApplicationBuildTemplate;
import hu.infostyle.parsedotclasspath.buildtemplate.AndroidLibraryBuildTemplate;
import hu.infostyle.parsedotclasspath.buildtemplate.BaseTemplate;
import hu.infostyle.parsedotclasspath.buildtemplate.EjbBuildTemplate;
import hu.infostyle.parsedotclasspath.eclipseutil.ClasspathUtil;
import hu.infostyle.parsedotclasspath.eclipseutil.EclipseProjectType;
import hu.infostyle.parsedotclasspath.eclipseutil.EnvironmentVariables;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class ParseDotClasspath {
    private static EnvironmentVariables environmentVariables;
    private static HashMap<String, Object> templateSettings;
    private static PropertyExporter propertyExporter;
    private static Set<String> kinds = new HashSet<String>(Arrays.asList(new String[]{"lib", "output", "src", "var"}));

    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {
        //The first argument must be the Eclispse workspace's absolute path
        environmentVariables = new EnvironmentVariables(args[0]);
        templateSettings = new HashMap<String, Object>();
        propertyExporter = new PropertyExporter();
        List<String> projectDirectories = new ArrayList<String>();

        for (int i = 1; i < args.length; i++) {
            projectDirectories.add(args[i].intern());
        }

        for(int i = 0; i < projectDirectories.size(); i++) {
            File dotClasspathFile = new File(projectDirectories.get(i), ClasspathUtil.CLASSPATHFILENAME);
            templateSettings.put("classpathName", new File(projectDirectories.get(i)).getName());
            StringBuffer stringBuffer = new StringBuffer();
            ClasspathBuilder classpathBuilder = new ClasspathBuilder();
            parseDotClasspath(dotClasspathFile, classpathBuilder);
            stringBuffer.append(dotClasspathFile.getParentFile().getName() + ".classpath=" + classpathBuilder.getResult());
            propertyExporter.addPath(projectDirectories.get(i));
            propertyExporter.addClasspath(ClasspathUtil.valueOf(stringBuffer.toString()));
            stringBuffer.setLength(0);
            EclipseProjectType projectType = ClasspathUtil.getProjectType(projectDirectories.get(i));
            switch (projectType) {
                case EJB: {
                    EjbBuildTemplate ejbBuildTemplate = new EjbBuildTemplate(projectDirectories.get(i) + File.separator + "gen_build.xml");
                    ejbBuildTemplate.init();
                    String classpathVariableName = new File(projectDirectories.get(i)).getName() + ".classpath";
                    ejbBuildTemplate.addClasspathElement(classpathVariableName);
                    ejbBuildTemplate.addInitTarget((String)templateSettings.get("classesDir"), (String)templateSettings.get("src"),
                                                   (List<String>)templateSettings.get("excludesList"), false);
                    ejbBuildTemplate.addBuildProjectTarget(true, (String)templateSettings.get("classesDir"),
                                                          (String)templateSettings.get("src"),
                                                           classpathVariableName);
                    List<String> dirsToDelete = new ArrayList<String>();
                    dirsToDelete.add((String)templateSettings.get("classesDir"));
                    ejbBuildTemplate.addCleanTarget(dirsToDelete);
                    ejbBuildTemplate.addCleanAllTarget();
                    ejbBuildTemplate.export();
                    templateSettings.clear();
                    break;
                }
                case ANDROID: {
                    //TODO Complete me
                    /*String currentProject = projectDirectories.get(i);
                    BaseTemplate template = isAndroidLibraryProject(currentProject) ?
                            new AndroidLibraryBuildTemplate(environmentVariables, currentProject + File.separator + "gen_build.xml") :
                            new AndroidApplicationBuildTemplate(environmentVariables, currentProject + File.separator + "gen_build.xml");*/
                }
                default:
                    templateSettings.clear();
                    break;
            }

        }
        propertyExporter.export(environmentVariables, args[0]);
    }

    private static File absolutizeFile(File base, String path) {
        path = path.replace('/', File.separatorChar);
        path = path.replace('\\', File.separatorChar);
        File child = new File(path);
        if (child.isAbsolute())
            return child;
        else
            return new File(base, path);
    }

    private static String makeAntVariableFromString(String variable) {
        return new StringBuilder("${").append(variable).append(".classpath}").toString();
    }

    private static void parseDotClasspath(File dotClasspathFile, final ClasspathBuilder classpathBuilder)
                                            throws ParserConfigurationException, SAXException, IOException {
        final File projectDirectory = dotClasspathFile.getParentFile().getAbsoluteFile();

        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        saxParserFactory.setNamespaceAware(true);
        XMLReader parser = saxParserFactory.newSAXParser().getXMLReader();
        parser.setContentHandler(new DefaultHandler() {
            public void startElement(String uri, String localname, String qname, Attributes atts) {
                if (!localname.equals("classpathentry"))
                    return;
                String cpEntryKind = atts.getValue("kind");
                if (cpEntryKind != null && kinds.contains(cpEntryKind)) {
                    String path = atts.getValue("path");
                    if (cpEntryKind.equals("var")) {
                        int i = path.indexOf("/");
                        String dir = environmentVariables.getVariableByKey(path.substring(0, i));
                        path = dir + File.separator + path.substring(i + 1);
                        classpathBuilder.add(absolutizeFile(projectDirectory, path));
                    } else if (cpEntryKind.equals("src")) {
                        if (path.startsWith("/")) {
                            String dependencyClasspathName = path.substring(1);
                            classpathBuilder.add(makeAntVariableFromString(dependencyClasspathName));
                        } else {
                            templateSettings.put("src", path);
                            String excludes = atts.getValue("excluding");
                            if (excludes != null) {
                                templateSettings.put("excludesList", Arrays.asList(excludes.split("\\|")));
                            }
                        }
                    } else if (cpEntryKind.equals("output")) {
                        templateSettings.put("classesDir", path);
                        classpathBuilder.add(absolutizeFile(projectDirectory, path));
                    } else if(cpEntryKind.equals("con")) {
                        return;
                    } else {
                        classpathBuilder.add(absolutizeFile(projectDirectory, path));
                    }
                }
            }
        });
        parser.parse(dotClasspathFile.toURI().toURL().toString());
    }

    private static boolean isAndroidLibraryProject(String projectHome) {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(projectHome + File.separator + "local.properties"));
            Boolean isLibrary = new Boolean(properties.getProperty("android.library"));
            if (isLibrary != null && isLibrary)
                return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
