package hu.infostyle.parsedotclasspath.test;

import hu.infostyle.parsedotclasspath.antutils.AntBuildWriter;
import hu.infostyle.parsedotclasspath.antutils.AntPropertyType;
import hu.infostyle.parsedotclasspath.antutils.AntUtils;
import hu.infostyle.parsedotclasspath.antutils.EjbBuildTemplate;
import hu.infostyle.parsedotclasspath.eclipseutils.ClasspathUtil;
import junit.framework.TestCase;

import java.util.ArrayList;

public class AntWriterTest extends TestCase {
    public void testPropertyWrite() {
        EjbBuildTemplate ejbBuildTemplate = new EjbBuildTemplate("D:/Work/is_base/gen_build.xml");
        ejbBuildTemplate.addPropertyFileElement(AntPropertyType.FILE, ClasspathUtil.PROPERTY_FILE_NAME);
        ejbBuildTemplate.addPropertyNameElement(AntPropertyType.NAME, AntUtils.BUILD_PROPERTY_DEBUGLEVEL, AntUtils.BUILD_PROPERTY_DEBUGLEVEL_VALUE);
        ejbBuildTemplate.addPropertyNameElement(AntPropertyType.NAME, AntUtils.BUILD_PROPERTY_TARGET, AntUtils.BUILD_PROPERTY_VALUE_CODETARGET);
        ejbBuildTemplate.addPropertyNameElement(AntPropertyType.NAME, AntUtils.BUILD_PROPERTY_SOURCE, AntUtils.BUILD_PROPERTY_SOURCE_VALUE);
        ejbBuildTemplate.addPropertyNameElement(AntPropertyType.NAME, AntUtils.BUILD_PROPERTY_ENCODING, AntUtils.BUILD_PROPERTY_ENCODING_TYPE);
        ejbBuildTemplate.addClasspathElement("is_base.classpath");
        ejbBuildTemplate.addInitTarget("build/classes", "src", null, false);
        ejbBuildTemplate.addBuildProjectTarget(true, "build/classes", "src", "is_base.classpath");
        ArrayList<String> dirsToDelete = new ArrayList<String>();
        dirsToDelete.add("build/classes");
        ejbBuildTemplate.addCleanTarget(dirsToDelete);
        ejbBuildTemplate.addCleanAllTarget();

        AntBuildWriter buildWriter = new AntBuildWriter();
        buildWriter.export(ejbBuildTemplate);
    }
}
