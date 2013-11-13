package hu.infostyle.parsedotclasspath.test;

import hu.infostyle.parsedotclasspath.antutils.AntBuildWriter;
import hu.infostyle.parsedotclasspath.antutils.AntPropertyType;
import hu.infostyle.parsedotclasspath.eclipseutils.ClasspathUtil;
import junit.framework.TestCase;

public class AntWriterTest extends TestCase {
    public void testPropertyWrite() {
        AntBuildWriter buildWriter = new AntBuildWriter("D:/Work/is_base/gen_build.xml");
        buildWriter.addPropertyElement(AntPropertyType.FILE, "gen_build.xml");
    }
}
