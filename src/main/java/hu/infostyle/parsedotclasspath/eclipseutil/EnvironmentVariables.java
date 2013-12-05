package hu.infostyle.parsedotclasspath.eclipseutil;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;

public class EnvironmentVariables {
    private HashMap<String, String> environmentVariables;

    public EnvironmentVariables() {
        environmentVariables = new HashMap<String, String>();
    }

    public EnvironmentVariables(String workspaceDir) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(workspaceDir).append(File.separator).append(ClasspathUtil.ORG_ECLIPSE_JDT_CORE_PREFS_DIR);
        stringBuilder.append(File.separator).append(ClasspathUtil.ORG_ECLIPSE_JDT_CORE_PREFS_FILE);
        File propertyFile = new File(stringBuilder.toString());
        try {
            FileInputStream fileInputStream = new FileInputStream(propertyFile);
            if (fileInputStream == null)
                return;
            Properties properties = new Properties();
            properties.load(fileInputStream);
            environmentVariables = new HashMap<String, String>();
            Set<String> propertyKeys = properties.stringPropertyNames();
            for (String propertyKey : propertyKeys) {
                String[] keyParts = propertyKey.split("\\.");
                if (isEclipseClasspathVariable(propertyKey))
                    environmentVariables.put(keyParts[keyParts.length - 1], properties.getProperty(propertyKey));
                continue;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException();
        }

    }

    public HashMap<String, String> getEnvironmentVariables() {
        return environmentVariables;
    }

    public void setEnvironmentVariables(HashMap<String, String> environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public void addVariableWithKey(String key, String variable) {
        environmentVariables.put(key, variable);
    }

    public String getVariableByKey(String key) {
        return environmentVariables.get(key);
    }

    public void deleteVariableWithKey(String key) {
        environmentVariables.remove(key);
    }

    public int size() {
        return environmentVariables.size();
    }

    private static boolean isEclipseClasspathVariable(String propertyKey) {
        return propertyKey.startsWith(ClasspathUtil.ECLIPSE_PREFS_CPVAR_PREFIX);
    }
}