package hu.infostyle.parsedotclasspath.test;

import hu.infostyle.parsedotclasspath.antutil.AntBuildWriter;
import hu.infostyle.parsedotclasspath.antutil.AntPropertyType;
import hu.infostyle.parsedotclasspath.antutil.AntUtils;
import hu.infostyle.parsedotclasspath.buildtemplate.EjbBuildTemplate;
import hu.infostyle.parsedotclasspath.eclipseutil.ClasspathUtil;
import junit.framework.TestCase;

import java.util.ArrayList;

public class AntWriterTest extends TestCase {
	
	//public static final String WS_ROOT = "D:/Work/base/dev/is_base/";
	/*public static final String WS_ROOT = "D:/work/";
	
    public void testIsBasePropertyWrite() {
        EjbBuildTemplate ejbBuildTemplate = new EjbBuildTemplate(WS_ROOT+"gen_build.xml");
        ejbBuildTemplate.addPropertyElement(AntPropertyType.FILE, null, ClasspathUtil.PROPERTY_FILE_NAME);
        ejbBuildTemplate.addPropertyElement(AntPropertyType.NAME, AntUtils.BUILD_PROPERTY_DEBUGLEVEL, AntUtils.BUILD_PROPERTY_DEBUGLEVEL_VALUE);
        ejbBuildTemplate.addPropertyElement(AntPropertyType.NAME, AntUtils.BUILD_PROPERTY_TARGET, AntUtils.BUILD_PROPERTY_VALUE_CODETARGET);
        ejbBuildTemplate.addPropertyElement(AntPropertyType.NAME, AntUtils.BUILD_PROPERTY_SOURCE, AntUtils.BUILD_PROPERTY_SOURCE_VALUE);
        ejbBuildTemplate.addPropertyElement(AntPropertyType.NAME, AntUtils.BUILD_PROPERTY_ENCODING, AntUtils.BUILD_PROPERTY_ENCODING_TYPE);
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
    
    public void testVtkBeansPropertyWrite() {
        EjbBuildTemplate ejbBuildTemplate = new EjbBuildTemplate(WS_ROOT+"gen_build.xml");
        ejbBuildTemplate.addPropertyElement(AntPropertyType.FILE, null, ClasspathUtil.PROPERTY_FILE_NAME);
        ejbBuildTemplate.addPropertyElement(AntPropertyType.NAME, AntUtils.BUILD_PROPERTY_DEBUGLEVEL, AntUtils.BUILD_PROPERTY_DEBUGLEVEL_VALUE);
        ejbBuildTemplate.addPropertyElement(AntPropertyType.NAME, AntUtils.BUILD_PROPERTY_TARGET, AntUtils.BUILD_PROPERTY_VALUE_CODETARGET);
        ejbBuildTemplate.addPropertyElement(AntPropertyType.NAME, AntUtils.BUILD_PROPERTY_SOURCE, AntUtils.BUILD_PROPERTY_SOURCE_VALUE);
        ejbBuildTemplate.addPropertyElement(AntPropertyType.NAME, AntUtils.BUILD_PROPERTY_ENCODING, AntUtils.BUILD_PROPERTY_ENCODING_TYPE);
        ejbBuildTemplate.addClasspathElement("vtk-beans.classpath");
        ejbBuildTemplate.addInitTarget("bin", "src", null, false);
        ejbBuildTemplate.addBuildProjectTarget(true, "bin", "src", "vtk-beans.classpath");
        ArrayList<String> dirsToDelete = new ArrayList<String>();
        dirsToDelete.add("bin");
        ejbBuildTemplate.addCleanTarget(dirsToDelete);
        ejbBuildTemplate.addCleanAllTarget();

        AntBuildWriter buildWriter = new AntBuildWriter();
        buildWriter.export(ejbBuildTemplate);
    }
    
    public void testVtkJpaPropertyWrite() {
        EjbBuildTemplate ejbBuildTemplate = new EjbBuildTemplate(WS_ROOT+"gen_build.xml");
        ejbBuildTemplate.addPropertyElement(AntPropertyType.FILE, null, ClasspathUtil.PROPERTY_FILE_NAME);
        ejbBuildTemplate.addPropertyElement(AntPropertyType.NAME, AntUtils.BUILD_PROPERTY_DEBUGLEVEL, AntUtils.BUILD_PROPERTY_DEBUGLEVEL_VALUE);
        ejbBuildTemplate.addPropertyElement(AntPropertyType.NAME, AntUtils.BUILD_PROPERTY_TARGET, AntUtils.BUILD_PROPERTY_VALUE_CODETARGET);
        ejbBuildTemplate.addPropertyElement(AntPropertyType.NAME, AntUtils.BUILD_PROPERTY_SOURCE, AntUtils.BUILD_PROPERTY_SOURCE_VALUE);
        ejbBuildTemplate.addPropertyElement(AntPropertyType.NAME, AntUtils.BUILD_PROPERTY_ENCODING, AntUtils.BUILD_PROPERTY_ENCODING_TYPE);
        ejbBuildTemplate.addClasspathElement("vtk-jpa.classpath");
        ejbBuildTemplate.addInitTarget("build/classes", "src", null, false);
        ejbBuildTemplate.addBuildProjectTarget(true, "build/classes", "src", "vtk-jpa.classpath");
        ArrayList<String> dirsToDelete = new ArrayList<String>();
        dirsToDelete.add("build/classes");
        ejbBuildTemplate.addCleanTarget(dirsToDelete);
        ejbBuildTemplate.addCleanAllTarget();

        AntBuildWriter buildWriter = new AntBuildWriter();
        buildWriter.export(ejbBuildTemplate);
    }    */
}
