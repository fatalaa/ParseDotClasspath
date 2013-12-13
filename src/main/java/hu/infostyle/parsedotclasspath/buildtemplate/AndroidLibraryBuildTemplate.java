package hu.infostyle.parsedotclasspath.buildtemplate;

import hu.infostyle.parsedotclasspath.antutil.AntExportable;
import hu.infostyle.parsedotclasspath.antutil.AntPropertyType;
import hu.infostyle.parsedotclasspath.eclipseutil.EnvironmentVariables;
import org.apache.commons.io.FileUtils;
import org.javatuples.Triplet;
import org.jdom2.Comment;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class AndroidLibraryBuildTemplate extends BaseTemplate implements AntExportable {
    protected String androidHome;
    protected List<Triplet<String, String, String>> refProjects;

    public AndroidLibraryBuildTemplate(String workspaceRootDir, EnvironmentVariables environmentVariables, String outputFileWithPath, List<Triplet<String, String, String>> refProjects) {
        super(workspaceRootDir, outputFileWithPath);
        this.refProjects = refProjects;
        if (environmentVariables != null) {
            androidHome = environmentVariables.getVariableByKey("ANDROID_HOME");
            return;
        }
        throw new RuntimeException("ANDROID_HOME is not set in Eclipse");
    }

    @Override
    public void createBuildFileWithProjectElement() {
        buildFileContent = new Document();
        Element element = new Element("project");
        element.setAttribute("name", outputFile.getParentFile().getName());
        element.setAttribute("default", "help");
        buildFileContent.setRootElement(element);

        String comment = "The local.properties file is created and updated by the 'android' tool." +
                         "It contains the path to the SDK. It should *NOT* be checked into" +
                         "Version Control Systems.";
        Comment commentElement = new Comment(comment);
        appendContentToBuildFile(buildFileContent, commentElement);

        addPropertyElement(AntPropertyType.FILE, null, "local.properties");

        comment = "The ant.properties file can be created by you. It is only edited by the\n" +
                  "\t'android' tool to add properties to it.\n" +
                  "\tThis is the place to change some Ant specific build properties.\n" +
                  "\tHere are some properties you may want to change/update:\n" +
                  "\n\tsource.dir\n" +
                  "\tThe name of the source directory. Default is 'src'.\n" +
                  "\n\tout.dir\n" +
                  "\tThe name of the output directory. Default is 'bin'.\n" +
                  "\n\tFor other overridable properties, look at the beginning of the rules\n" +
                  "\tfiles in the SDK, at tools/ant/build.xml\n" +
                  "\n\tProperties related to the SDK location or the project target should\n" +
                  "\tbe updated using the 'android' tool with the 'update' action.\n" +
                  "\n\tThis file is an integral part of the build system for your\n" +
                  "\tapplication and should be checked into Version Control Systems.";
        appendContentToBuildFile(buildFileContent, new Comment(comment));

        addPropertyElement(AntPropertyType.FILE, null, "ant.properties");

        comment = "if sdk.dir was not set from one of the property file, then\n" +
                "\t\tget it from the ANDROID_HOME env var.\n" +
                "\t\tThis must be done before we load project.properties since\n" +
                "\t\tthe proguard config can use sdk.dir";
        appendContentToBuildFile(buildFileContent, new Comment(comment));

        appendContentToBuildFile(buildFileContent, new Element("property").setAttribute("environment", "env"));

        Element condition = new Element("condition").setAttribute("property", "sdk.dir").setAttribute("value", "${env.ANDROID_HOME}");
        Element isset = new Element("isset").setAttribute("property", "env.ANDROID_HOME");
        condition.addContent(isset);
        appendContentToBuildFile(buildFileContent, condition);

        comment = "The project.properties file is created and updated by the 'android'\n" +
                  "\ttool, as well as ADT.\n" +
                  "\n\tThis contains project specific properties such as project target, and library\n" +
                  "\tdependencies. Lower level build properties are stored in ant.properties\n" +
                  "\t(or in .classpath for Eclipse projects).\n" +
                  "\n\tThis file is an integral part of the build system for your\n" +
                  "\tapplication and should be checked into Version Control Systems.";
        appendContentToBuildFile(buildFileContent, new Comment(comment));

        appendContentToBuildFile(buildFileContent, new Element("loadproperties").setAttribute("srcFile", "project.properties"));

        appendContentToBuildFile(buildFileContent, new Comment("quick check on sdk.dir"));

        comment = "sdk.dir is missing. Make sure to generate local.properties using 'android update project' or to inject it through the ANDROID_HOME environment variable.";
        appendContentToBuildFile(buildFileContent, new Element("fail").setAttribute("message", comment).setAttribute("unless", "sdk.dir"));

        comment = "Import per project custom build rules if present at the root of the project.\n" +
                "        This is the place to put custom intermediary targets such as:\n" +
                "            -pre-build\n" +
                "            -pre-compile\n" +
                "            -post-compile (This is typically used for code obfuscation.\n" +
                "                           Compiled code location: ${out.classes.absolute.dir}\n" +
                "                           If this is not done in place, override ${out.dex.input.absolute.dir})\n" +
                "            -post-package\n" +
                "            -post-build\n" +
                "            -pre-clean";
        appendContentToBuildFile(buildFileContent, new Comment(comment));

        appendContentToBuildFile(buildFileContent, new Element("import").setAttribute("file", "custom_rules.xml").setAttribute("optional", "true"));

        comment = "Import the actual build file.\n" +
                "\n" +
                "         To customize existing targets, there are two options:\n" +
                "         - Customize only one target:\n" +
                "             - copy/paste the target into this file, *before* the\n" +
                "               <import> task.\n" +
                "             - customize it to your needs.\n" +
                "         - Customize the whole content of build.xml\n" +
                "             - copy/paste the content of the rules files (minus the top node)\n" +
                "               into this file, replacing the <import> task.\n" +
                "             - customize to your needs.\n" +
                "\n" +
                "         ***********************\n" +
                "         ****** IMPORTANT ******\n" +
                "         ***********************\n" +
                "         In all cases you must update the value of version-tag below to read 'custom' instead of an integer,\n" +
                "         in order to avoid having your file be overridden by tools such as \"android update project\"";
        appendContentToBuildFile(buildFileContent, new Comment(comment));

        appendContentToBuildFile(buildFileContent, new Comment("version-tag: 1"));

        appendContentToBuildFile(buildFileContent, new Element("import").setAttribute("file", "${sdk.dir}/tools/ant/build.xml"));
    }

    public boolean executeUpdateOnProject(int targetApiLevelId) {
        StringBuilder stringBuilder = new StringBuilder(androidHome);
        stringBuilder.append("/tools/android.bat -v update lib-project -p ").append(getProjectHome())
                .append(String.format(" -t %s", targetApiLevelId));
        try {
            Process process = Runtime.getRuntime().exec(stringBuilder.toString());
            int exitValue = process.waitFor();
            return exitValue == 0;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(String.format("Cannot execute %s command", stringBuilder.toString()));
        } catch (InterruptedException exception) {
            exception.printStackTrace();
            throw new RuntimeException(String.format("Cannot execute %s command", stringBuilder.toString()));
        }
    }

    @Override
    public void export() {
        StringWriter stringWriter = new StringWriter();
        XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
        try {
            xmlOutputter.output(buildFileContent, stringWriter);
            if (outputFile.exists()) {
                if (outputFile.delete()) {
                    System.out.println(outputFile.getAbsoluteFile().getName() + " deleted");
                } else {
                    System.out.println(outputFile.getAbsoluteFile().getName() + " not deleted");
                }
            }
            FileUtils.writeStringToFile(outputFile, stringWriter.toString(), true);
        } catch (IOException exception) {
            exception.printStackTrace();
            throw new RuntimeException("Cannot export project");
        }
    }

    public void addSpecificationToProject() {
        if (outputFile.exists() && !outputFile.delete())
            throw new RuntimeException("Can not delete existing buildfile");
        try {
            createBuildFileWithProjectElement();
            export();
            buildFileContent = new SAXBuilder().build(outputFile);
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Element rootElement = buildFileContent.getRootElement();
        Element globalPropertyElement = new Element("property").setAttribute("file", workspaceRootDir + "/gen_global.properties");
        int idx = 0;
        for (int i = 0 ; i < rootElement.getChildren().size(); i++) {
            if (rootElement.getChildren().get(i).getAttributeValue("file").equals("local.properties")) {
                rootElement.getChildren().add(i + 1, globalPropertyElement);
                System.out.println("Global property element added");
                idx = i + 1;
                break;
            }
        }
        Element javaCompilerProperty = new Element("property").setAttribute("name", "java.compiler.classpath")
                .setAttribute("value", "${" + getProjectName() + ".classpath}");
        rootElement.getChildren().add(idx + 1, javaCompilerProperty);
        if (refProjects.size() > 0) {
            List<Element> deletes = new ArrayList<Element>();
            XPathExpression<Element> expression = XPathFactory.instance().compile("//import", Filters.element());
            for(Element element : expression.evaluate(buildFileContent)) {
                if (element.getAttributes().size() > 1 )
                    idx = buildFileContent.getRootElement().indexOf(element);
            }
            Element preBuildTarget = new Element("target").setAttribute("name", "-pre-build");
            for(Triplet<String, String, String> refProject : refProjects) {
                Element buildRefProjectElement = new Element("ant").setAttribute("antfile", "build.xml")
                        .setAttribute("dir", refProject.getValue1()).setAttribute("inheritAll", "false").setAttribute("target", "clean");
                Element cleanRefProjectElement = new Element("ant").setAttribute("antfile", "build.xml")
                        .setAttribute("dir", refProject.getValue1()).setAttribute("inheritAll", "false").setAttribute("target", "jar");
                String jarname = refProject.getValue1().substring(refProject.getValue1().lastIndexOf('/') + 1) + ".jar";
                Element copyRefProjectJarElement = new Element("copy").setAttribute("todir", "libs").setAttribute("file", refProject.getValue1() + "/" + jarname);
                Element deleteElement = new Element("delete").setAttribute("file", refProject.getValue1() + "/" + jarname);
                Element deleteFromLibsElement = new Element("delete").setAttribute("file", "libs/" + jarname);
                deletes.add(deleteFromLibsElement);
                deletes.add(deleteElement);
                preBuildTarget.addContent(buildRefProjectElement);
                preBuildTarget.addContent(cleanRefProjectElement);
                preBuildTarget.addContent(copyRefProjectJarElement);
            }
            buildFileContent.getRootElement().addContent(idx - 1, preBuildTarget);

            Element preCleanElement = new Element("target").setAttribute("name", "-pre-clean");
            preCleanElement.addContent(deletes);
            buildFileContent.getRootElement().addContent(idx - 1, preCleanElement);
        }
    }
}
