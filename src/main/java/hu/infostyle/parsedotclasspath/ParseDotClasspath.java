package hu.infostyle.parsedotclasspath;

import hu.infostyle.parsedotclasspath.buildtemplate.AndroidApplicationBuildTemplate;
import hu.infostyle.parsedotclasspath.buildtemplate.AndroidLibraryBuildTemplate;
import hu.infostyle.parsedotclasspath.buildtemplate.EjbBuildTemplate;
import hu.infostyle.parsedotclasspath.eclipseutil.ClasspathUtil;
import hu.infostyle.parsedotclasspath.eclipseutil.EclipseProjectType;
import hu.infostyle.parsedotclasspath.eclipseutil.EnvironmentVariables;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.apache.commons.lang3.tuple.ImmutablePair;

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
    private static StringBuffer stringBuffer;
    private static List<ImmutablePair<String, String>> refProjects = new ArrayList<ImmutablePair<String, String>>();
    private static List<String> projectDirectories = new ArrayList<String>();

    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {
        //The first argument must be the Eclispse workspace's absolute path
        environmentVariables = new EnvironmentVariables(args[0]);
        templateSettings = new HashMap<String, Object>();
        propertyExporter = new PropertyExporter();


        for (int i = 1; i < args.length; i++) {
            projectDirectories.add(args[i].intern());
        }

        for(int i = 0; i < projectDirectories.size(); i++) {
            File dotClasspathFile = new File(projectDirectories.get(i), ClasspathUtil.CLASSPATHFILENAME);
            templateSettings.put("classpathName", new File(projectDirectories.get(i)).getName());
            stringBuffer = new StringBuffer();
            ClasspathBuilder classpathBuilder = new ClasspathBuilder();
            EclipseProjectType projectType = ClasspathUtil.getProjectType(projectDirectories.get(i));
            parseDotClasspath(dotClasspathFile, classpathBuilder, projectType);
            stringBuffer.append(dotClasspathFile.getParentFile().getName() + ".classpath=" + classpathBuilder.getResult());


            switch (projectType) {
                case EJB: {
                    EjbBuildTemplate ejbBuildTemplate = new EjbBuildTemplate(args[0], projectDirectories.get(i) + File.separator + "build.xml");
                    ejbBuildTemplate.init();
                    String classpathVariableName = new File(projectDirectories.get(i)).getName() + ".classpath";
                    ejbBuildTemplate.addClasspathElement(classpathVariableName);
                    ejbBuildTemplate.addInitTarget((String)templateSettings.get("classesDir"), (String)templateSettings.get("src"),
                                                   (List<String>)templateSettings.get("excludesList"), false);
                    ejbBuildTemplate.addBuildProjectTarget(true, (String)templateSettings.get("classesDir"),
                                                          (String)templateSettings.get("src"),
                                                           classpathVariableName);
                    if (refProjects.size() > 0)
                        ejbBuildTemplate.addBuildAllTarget(refProjects);
                    List<String> dirsToDelete = new ArrayList<String>();
                    dirsToDelete.add((String)templateSettings.get("classesDir"));
                    ejbBuildTemplate.addCleanTarget(dirsToDelete);
                    ejbBuildTemplate.addCleanAllTarget();
                    ejbBuildTemplate.export();
                    templateSettings.clear();
                    refProjects.clear();
                    break;
                }
                case ANDROID: {
                    //TODO Complete me
                    String currentProject = projectDirectories.get(i);
                    if (isAndroidLibraryProject(currentProject)) {
                        AndroidLibraryBuildTemplate template = new AndroidLibraryBuildTemplate(args[0], environmentVariables,
                                                               currentProject + File.separator + "build.xml");
                        template.addSpecificationToProject();
                        template.export();
                    }
                    else {
                        AndroidApplicationBuildTemplate template = new AndroidApplicationBuildTemplate(args[0], environmentVariables,
                                                                   currentProject + File.separator + "build.xml");
                    }
                    refProjects.clear();
                    break;
                }
                default:
                    templateSettings.clear();
                    refProjects.clear();
                    break;
            }
            propertyExporter.addPath(projectDirectories.get(i));
            propertyExporter.addClasspath(ClasspathUtil.valueOf(stringBuffer.toString()));
            stringBuffer.setLength(0);
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

    private static void parseDotClasspath(File dotClasspathFile, final ClasspathBuilder classpathBuilder, EclipseProjectType projectType)
                                            throws ParserConfigurationException, SAXException, IOException {
        final File projectDirectory = dotClasspathFile.getParentFile().getAbsoluteFile();

        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        saxParserFactory.setNamespaceAware(true);
        XMLReader parser = saxParserFactory.newSAXParser().getXMLReader();
        switch (projectType) {
            case EJB:
                parser.setContentHandler(new EjbHandler(projectDirectory, classpathBuilder));
                break;
            case ANDROID:
                parser.setContentHandler(new AndroidHandler(projectDirectory, classpathBuilder, stringBuffer));
                break;
            default:
                throw new RuntimeException("Cannot handle this type of project");
        }
        parser.parse(dotClasspathFile.toURI().toURL().toString());
    }

    private static boolean isAndroidLibraryProject(String projectDirectory) {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(projectDirectory + File.separator + "project.properties"));
            Boolean isLibrary = new Boolean(properties.getProperty("android.library"));
            if (isLibrary != null && isLibrary)
                return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static List<String> getAndroidDependencies(final File projectDirectory) {
        Properties projectProperties = new Properties();
        try {
            projectProperties.load(new FileInputStream(projectDirectory.getAbsolutePath() + File.separator + "project.properties"));
            ArrayList<String> androidDependencies = new ArrayList<String>();
            Enumeration projectPropertiesKeys = projectProperties.keys();
            while (projectPropertiesKeys.hasMoreElements()) {
                String key = (String)projectPropertiesKeys.nextElement();
                if (key.contains("android.library.reference")) {
                    androidDependencies.add(projectProperties.getProperty(key));
                }
            }
            return androidDependencies.size() > 0 ? androidDependencies : null;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(String.format("Can not get android dependencies for project: %s", projectDirectory.getName()));
        }
    }

    private static String getRefProjectDirectory(String projectName, List<String> projectPaths) {
        for(int i = 0; i < projectPaths.size(); i++) {
            if (projectPaths.get(i).contains(projectName))
                return projectPaths.get(i);
        }
        return null;
    }

    private static class EjbHandler extends DefaultHandler {
        protected File projectDirectory;
        protected ClasspathBuilder classpathBuilder;

        public EjbHandler(File projectDirectory, ClasspathBuilder classpathBuilder) {
            this.projectDirectory = projectDirectory;
            this.classpathBuilder = classpathBuilder;
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (!localName.equals("classpathentry"))
                return;
            String cpEntryKind = attributes.getValue("kind");
            if (cpEntryKind != null && kinds.contains(cpEntryKind)) {
                String path = attributes.getValue("path");
                if (cpEntryKind.equals("var")) {
                    int i = path.indexOf("/");
                    String dir = environmentVariables.getVariableByKey(path.substring(0, i));
                    path = dir + File.separator + path.substring(i + 1);
                    classpathBuilder.add(absolutizeFile(projectDirectory, path));
                } else if (cpEntryKind.equals("src")) {
                    if (path.startsWith("/")) {
                        String dependencyClasspathName = path.substring(1);
                        classpathBuilder.add(makeAntVariableFromString(dependencyClasspathName));
                        refProjects.add(new ImmutablePair<String, String>(dependencyClasspathName, getRefProjectDirectory(dependencyClasspathName, projectDirectories)));
                    } else {
                        templateSettings.put("src", path);
                        String excludes = attributes.getValue("excluding");
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
    }

    private static class AndroidHandler extends EjbHandler {
        private StringBuffer stringBuffer;

        public AndroidHandler(File projectDirectory, ClasspathBuilder classpathBuilder, StringBuffer stringBuffer) {
            super(projectDirectory, classpathBuilder);
            this.stringBuffer = stringBuffer;
        }

        @Override
        public void startDocument() throws SAXException {
            super.startDocument();
            List<String> dependencies = getAndroidDependencies(projectDirectory);
            if (dependencies != null) {
                for(String dependency : dependencies) {
                    classpathBuilder.add(makeAntVariableFromString(dependency.substring(dependency.lastIndexOf("/") + 1)));
                }
                String androidJarPath = environmentVariables.getVariableByKey("ANDROID_HOME") + File.separator + "platforms" + File.separator + "android-8" + File.separator + "android.jar";
                classpathBuilder.add(new File(androidJarPath));
                String mapsApiJarPath = environmentVariables.getVariableByKey("ANDROID_HOME") + File.separator + "add-ons" + File.separator + "addon-google_apis-google-8" + File.separator + "libs" + File.separator + "maps.jar";
                classpathBuilder.add(new File(mapsApiJarPath));
            }
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (!localName.equals("classpathentry"))
                return;
            String cpEntryKind = attributes.getValue("kind");
            if (cpEntryKind != null && kinds.contains(cpEntryKind)) {
                String path = attributes.getValue("path");
                if (cpEntryKind.equals("var")) {
                    int i = path.indexOf("/");
                    String dir = environmentVariables.getVariableByKey(path.substring(0, i));
                    path = dir + File.separator + path.substring(i + 1);
                    classpathBuilder.add(absolutizeFile(projectDirectory, path));
                } else if (cpEntryKind.equals("src")) {
                    if (path.startsWith("/")) {
                        String dependencyClasspathName = path.substring(1);
                        refProjects.add(new ImmutablePair<String, String>(dependencyClasspathName, projectDirectory.getAbsolutePath()));
                    } else {
                        templateSettings.put("src", path);
                        String excludes = attributes.getValue("excluding");
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
    }
}
