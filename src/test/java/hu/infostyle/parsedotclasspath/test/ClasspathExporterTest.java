package hu.infostyle.parsedotclasspath.test;

import hu.infostyle.parsedotclasspath.ClasspathExporter;
import hu.infostyle.parsedotclasspath.util.ClasspathUtil;
import junit.framework.TestCase;

public class ClasspathExporterTest extends TestCase{

    public void testExport() {
        ClasspathExporter exporter = new ClasspathExporter();
        exporter.setProjectPath("/home/molnart/workspace/is_base");
        exporter.setClasspath(ClasspathUtil.valueOf("is_base.classpath=/home/molnart/glassfish3/glassfish/modules/jackson-core-asl.jar:/home/molnart/glassfish3/glassfish/modules/jackson-mapper-asl.jar:/home/molnart/workspace/is_base/build/classes"));
        exporter.export();
    }
}
