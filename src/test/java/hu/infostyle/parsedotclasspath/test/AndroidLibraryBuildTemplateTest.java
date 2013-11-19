package hu.infostyle.parsedotclasspath.test;

import hu.infostyle.parsedotclasspath.buildtemplate.AndroidLibraryBuildTemplate;
import hu.infostyle.parsedotclasspath.eclipseutils.EnvironmentVariables;
import junit.framework.TestCase;

public class AndroidLibraryBuildTemplateTest extends TestCase {
    public void testUpdate() {
        EnvironmentVariables environmentVariables = new EnvironmentVariables("d:\\Work\\");
        AndroidLibraryBuildTemplate template = new AndroidLibraryBuildTemplate(environmentVariables);
        template.setProjectHome("d:\\Work\\com_facebook_android\\");
        assertEquals(true, template.executeUpdateOnProject());
    }
}
