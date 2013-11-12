package hu.infostyle.parsedotclasspath;

import hu.infostyle.parsedotclasspath.util.EnvironmentVariables;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClasspathExporter {
	private List<String> paths = new ArrayList<String>();
	private List<HashMap<String, String>> classpaths = new ArrayList<HashMap<String, String>>();
	
	public void addPath(String path) {
		paths.add(path);
	}

	public void addClasspath(HashMap<String, String> classpath) {
		classpaths.add(classpath);
	}

	public void export(EnvironmentVariables environmentVariables) throws Exception{
		FileWriter fw = null;
		try {
			fw = new FileWriter("out.properties");
			
			HashMap<String, String> ev = environmentVariables.getEnvironmentVariables();
			for (String key : ev.keySet()) {
				fw.append(key).append("=").append(ev.get(key)).append("\r\n");
			}
			
			fw.append("\r\n");
			
			for (String key : paths) {
				fw.append(key).append("\r\n");
			}
			
			for (HashMap<String, String> p : classpaths) {
				for (String key : p.keySet()) {
					fw.append(key).append("=").append(p.get(key)).append("\r\n");
				}
			}
			
		} finally {
			if (fw != null) {
				fw.close();
			}
		}
	}

}
