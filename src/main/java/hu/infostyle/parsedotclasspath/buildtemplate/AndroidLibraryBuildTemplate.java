package hu.infostyle.parsedotclasspath.buildtemplate;

import hu.infostyle.parsedotclasspath.eclipseutils.EnvironmentVariables;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

public class AndroidLibraryBuildTemplate {
    private static String androidHome;
    private String projectHome;

    public String getProjectHome() {
        return projectHome;
    }

    public void setProjectHome(String projectHome) {
        this.projectHome = projectHome;
    }

    public AndroidLibraryBuildTemplate(EnvironmentVariables environmentVariables) {
        if (environmentVariables != null) {
            androidHome = environmentVariables.getVariableByKey("ANDROID_HOME");
            return;
        }
        throw new RuntimeException("ANDROID_HOME is not set in Eclipse");
    }

    public boolean executeUpdateOnProject() {
        StringBuilder stringBuilder = new StringBuilder(androidHome);
        stringBuilder.append("//tools//android.bat -v update lib-project -p .").append(projectHome).append(" -t 1");
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



    /*private Document openProjectBuildXml() {
        SAXBuilder saxBuilder = new SAXBuilder();
        File buildFile = new File(projectHome + File.separator + "build.xml");
        try {
            Document project = saxBuilder.build(buildFile);
            Element rootElement = project.getRootElement();

        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    private Properties openProjectPropertyFile() {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(projectHome + File.separator + "project.properties"));
            return properties;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Can not open project.properties");
        }
    }

    private List<String> getDependecyProjects() {
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
}
