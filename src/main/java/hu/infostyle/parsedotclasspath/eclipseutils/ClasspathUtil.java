package hu.infostyle.parsedotclasspath.eclipseutils;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    public static final String COMMENT_LINE = new StringBuilder("##################################################")
                                                            .append(System.getProperty("line.separator")).toString();

    public static HashMap<String,String> valueOf(String classpathAsString) {
        HashMap<String, String> variables = new HashMap<String, String>();
        String[] strings = classpathAsString.split("=");
        for (int i = 0, j = 1; j <= strings.length - 1; i++, j++) {
            variables.put(strings[i], strings[j]);
        }
        return variables;
    }

    public static EclipseProjectType getProjectType(String projectPath) {
        File classpathFile = new File(projectPath);
        SAXBuilder saxBuilder = new SAXBuilder();
        try {
            Document document = saxBuilder.build(classpathFile);
            Element natures = document.getRootElement();
            if (natures != null) {
                for(Element nature : natures.getChildren()) {
                    String natureKind = nature.getValue();
                    if (natureKind.equals("com.android.ide.eclipse.adt.AndroidNature"))
                        return EclipseProjectType.ANDROID;
                    else if (natureKind.equals("com.google.gwt.eclipse.core.gwtNature"))
                        return EclipseProjectType.GWT;
                }
                return EclipseProjectType.EJB;
            }
        } catch (JDOMException e) {
            e.printStackTrace();
            throw new RuntimeException("Cannot get project kind");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Cannot get project kind");
        } finally {
            return EclipseProjectType.UNKNOWN;
        }
    }

    private static Element getNaturesElement(Element rootElement) {
        List<Element> children = rootElement.getChildren();
        for(Element child : children) {
            if (child.getName().equals("natures"))
                return child;
        }
        return null;
    }
}
