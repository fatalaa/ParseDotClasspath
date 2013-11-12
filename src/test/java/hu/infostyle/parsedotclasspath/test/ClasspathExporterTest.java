package hu.infostyle.parsedotclasspath.test;

import hu.infostyle.parsedotclasspath.ClasspathExporter;
import hu.infostyle.parsedotclasspath.eclipseutils.ClasspathUtil;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClasspathExporterTest extends TestCase{

    public void testExport() {
        ClasspathExporter exporter = new ClasspathExporter();
        List<String> projectPaths = new ArrayList<String>();
        projectPaths.add("/home/molnart/workspace/is_base");
        exporter.setProjectPaths(projectPaths);
        List<HashMap<String, String >> classpaths = new ArrayList<HashMap<String, String>>();
        classpaths.add(ClasspathUtil.valueOf("is_base.classpath=/home/molnart/glassfish3/glassfish/modules/jackson-core-asl.jar:/home/molnart/glassfish3/glassfish/modules/jackson-mapper-asl.jar:/home/molnart/workspace/is_base/build/classes"));
        exporter.setClasspaths(classpaths);
        exporter.export();
    }
}
