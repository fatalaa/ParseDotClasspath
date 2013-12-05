package hu.infostyle.parsedotclasspath;

import hu.infostyle.parsedotclasspath.eclipseutil.ClasspathUtil;
import hu.infostyle.parsedotclasspath.eclipseutil.EnvironmentVariables;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PropertyExporter {
    private List<String> projectPaths = new ArrayList<String>();
    private List<HashMap<String, String>> classpaths = new ArrayList<HashMap<String, String>>();

    public List<String> getPaths() {
        return projectPaths;
    }

    public void setProjectPaths(List<String> paths) {
        this.projectPaths = paths;
    }

    public List<HashMap<String, String>> getClasspaths() {
        return classpaths;
    }

    public void setClasspaths(List<HashMap<String, String>> classpaths) {
        this.classpaths = classpaths;
    }

    public void addPath(String path) {
        projectPaths.add(path);
    }

    public void addClasspath(HashMap<String, String> classpath) {
        classpaths.add(classpath);
    }

    public String getClasspathValueByName(String classpathName) {
        for(HashMap<String, String> classpath : classpaths) {
            if (classpath.containsKey(classpathName))
                return classpath.get(classpathName);
        }
        return null;
    }

    public void export(EnvironmentVariables environmentVariables, String workspacePath) {
        FileWriter fileWriter = null;
        String workspaceDirectory = workspacePath;
        File outputFile = new File(workspaceDirectory, ClasspathUtil.PROPERTY_FILE_NAME);
        //outputFile = new File(ClasspathUtil.PROPERTY_FILE_NAME);
        try {
            fileWriter = new FileWriter(outputFile);

            HashMap<String, String> ev = environmentVariables.getEnvironmentVariables();
            for (String key : ev.keySet()) {
                fileWriter.append(key).append("=").append(ev.get(key)).append(System.getProperty("line.separator"));
            }

            fileWriter.append(ClasspathUtil.COMMENT_LINE);

            for (HashMap<String, String> classpath : classpaths) {
                for (String key : classpath.keySet()) {
                    fileWriter.append(key).append("=").append(classpath.get(key).replace("\\", "/")).append(System.getProperty("line.separator"));
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException("Exporting to property file failed");
        }
        finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
