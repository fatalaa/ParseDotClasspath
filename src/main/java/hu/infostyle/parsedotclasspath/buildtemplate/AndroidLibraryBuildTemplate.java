package hu.infostyle.parsedotclasspath.buildtemplate;

import hu.infostyle.parsedotclasspath.antutil.AntExportable;
import hu.infostyle.parsedotclasspath.eclipseutil.EnvironmentVariables;
import org.apache.commons.io.FileUtils;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

public class AndroidLibraryBuildTemplate extends BaseTemplate implements AntExportable {
    private String androidHome;

    public AndroidLibraryBuildTemplate(String workspaceRootDir, EnvironmentVariables environmentVariables, String outputFileWithPath) {
        super(workspaceRootDir, outputFileWithPath);
        if (environmentVariables != null) {
            androidHome = environmentVariables.getVariableByKey("ANDROID_HOME");
            return;
        }
        throw new RuntimeException("ANDROID_HOME is not set in Eclipse");
    }

    public boolean executeUpdateOnProject(int targetApiLevelId) {
        StringBuilder stringBuilder = new StringBuilder(androidHome);
        stringBuilder.append("/tools/android.bat -v update lib-project -p ").append(getProjectHome())
                .append(String.format(" -t %s", targetApiLevelId));
        try {
            Process process = Runtime.getRuntime().exec(stringBuilder.toString());
            int exitValue = process.waitFor();
            if (exitValue == 0 )
                return true;
            return false;
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

    private Properties openProjectPropertyFile() {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(getProjectHome() + File.separator + "project.properties"));
            return properties;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Can not open project.properties");
        }
    }

    private List<String> getDependencyProjects() {
        Properties properties = openProjectPropertyFile();
        Enumeration keys = properties.keys();
        List<String> references = new ArrayList<String>();
        while(keys.hasMoreElements()) {
            String key = (String)keys.nextElement();
            if (key.startsWith("android.library.reference."))
                references.add(properties.getProperty(key));
        }
        return references;
    }

    public void addSpecificationToProject() {
        if (!outputFile.delete() || !executeUpdateOnProject(2))
            throw new RuntimeException("Can not update project");
        try {
            buildFileContent = new SAXBuilder().build(outputFile);
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Element rootElement = buildFileContent.getRootElement();
        Element globalPropertyElement = new Element("property").setAttribute("file", workspaceRootDir+File.separator+"/gen_global.properties");
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
    }
}
