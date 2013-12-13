package hu.infostyle.parsedotclasspath.propertyfileutil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

public class PropertyUtil {
    public static String getValueForKey(File propertyFile, String key) {
        if (propertyFile.exists()) {
            Properties properties = new Properties();
            try {
                properties.load(new FileInputStream(propertyFile));
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            if (properties.size() <= 0)
                return null;
            String value = properties.getProperty(key);
            return value != null ? value : null;
        }
        return null;
    }

    public static String getValueForKey(String propertyFilePath, String key) {
        return getValueForKey(new File(propertyFilePath), key);
    }

    public static String getValueForKeyContainingString(File propertyFile, String containingKey) {
        if (propertyFile.exists()) {
            Properties properties = new Properties();
            try {
                properties.load(new FileInputStream(propertyFile));
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            if (properties.size() <= 0)
                return null;
            Enumeration propertyKeySet = properties.keys();
            String key = (String)propertyKeySet.nextElement();
            while (key != null) {
                if (key.contains(containingKey))
                    return properties.getProperty(key);
            }
        }
        return null;
    }

    public static String getValueForKeyContainingString(String propertyFilePath, String containingKey) {
        return getValueForKeyContainingString(new File(propertyFilePath), containingKey);
    }
}
