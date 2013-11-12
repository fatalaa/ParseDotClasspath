package hu.infostyle.parsedotclasspath;

import hu.infostyle.parsedotclasspath.eclipseutils.ClasspathUtil;
import hu.infostyle.parsedotclasspath.eclipseutils.EnvironmentVariables;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClasspathExporter {
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

	public void export(EnvironmentVariables environmentVariables) {
		FileWriter fileWriter = null;
        String workspaceDirectory = new File(projectPaths.get(0)).getParentFile().getAbsolutePath();
        File outputFile = new File(workspaceDirectory, ClasspathUtil.PROPERTY_FILE_NAME);
		try {
			fileWriter = new FileWriter(outputFile);
			
			HashMap<String, String> ev = environmentVariables.getEnvironmentVariables();
			for (String key : ev.keySet()) {
				fileWriter.append(key).append("=").append(ev.get(key)).append(System.getProperty("line.separator"));
			}
			
			fileWriter.append(ClasspathUtil.COMMENT_LINE);
			
			for (HashMap<String, String> classpath : classpaths) {
				for (String key : classpath.keySet()) {
					fileWriter.append(key).append("=").append(classpath.get(key)).append(System.getProperty("line.separator"));
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
