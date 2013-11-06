package hu.infostyle.parsedotclasspath.util;

import java.util.HashMap;

public class ClasspathUtil {
    public static final String ORG_ECLIPSE_JDT_CORE_PREFS_DIR = ".metadata/.plugins/org.eclipse.core.runtime/.settings";
    public static final String ORG_ECLIPSE_JDT_CORE_PREFS_FILE = "org.eclipse.jdt.core.prefs";
    public static final String ECLIPSE_PREFS_CPVAR_PREFIX = "org.eclipse.jdt.core.classpathVariable";
    public static final String CP_ELEMENT_CPENTRY = "classpathentry";
    public static final String CP_ATTR_KIND = "kind";
    public static final String CP_ATTR_KIND_PATH = "path";
    public static final String CP_ATTR_KIND_VAR = "var";

    //Eclipse inner classpath variables
    public static final String ANDROID_HOME = "ANDROID_HOME";
    public static final String GF_HOME = "GF_HOME";
    public static final String GWT = "GWT";
    public static final String SMARTGTW = "SMARTGWT";

    public static final String PROPERTY_FILE_NAME = "gen_global.properties";

    public static HashMap<String,String> valueOf(String classpathAsString) {
        HashMap<String, String> variables = new HashMap<String, String>();
        String[] strings = classpathAsString.split("=");
        for (int i = 0, j = 1; j <= strings.length - 1; i++, j++) {
            variables.put(strings[i], strings[j]);
        }
        return variables;
    }
}
