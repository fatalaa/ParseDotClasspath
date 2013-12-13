package hu.infostyle.parsedotclasspath.buildtemplate;

import hu.infostyle.parsedotclasspath.antutil.AntExportable;
import hu.infostyle.parsedotclasspath.eclipseutil.EnvironmentVariables;
import hu.infostyle.parsedotclasspath.propertyfileutil.PropertyUtil;
import org.apache.commons.io.FileUtils;
import org.javatuples.Triplet;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

public class AndroidApplicationBuildTemplate extends AndroidLibraryBuildTemplate implements AntExportable {

    public AndroidApplicationBuildTemplate(String workspaceRootDir, EnvironmentVariables environmentVariables, String outputFileWithPath, List<Triplet<String, String, String>> refProjects) {
        super(workspaceRootDir, environmentVariables, outputFileWithPath, refProjects);
    }

    @Override
    public void addSpecificationToProject() {
        super.addSpecificationToProject();
        String keystorePath = PropertyUtil.getValueForKey(getProjectHome() + "/" + "ant.properties", "key.store");
        if (keystorePath != null) {
            int idx = buildFileContent.indexOf(new Element("property").setAttribute("file", "ant.properties"));
            buildFileContent.getRootElement().addContent(idx + 1, new Element("property").setAttribute("name", "has.keystore").setAttribute("value", "1"));
        }
    }

    @Override
    public boolean executeUpdateOnProject(int targetApiLevelId) {
        StringBuilder stringBuilder = new StringBuilder(androidHome);
        stringBuilder.append("/tools/android.bat -v update project -p ").append(getProjectHome())
                .append(String.format(" -t %s", targetApiLevelId));
        try {
            Process process = Runtime.getRuntime().exec(stringBuilder.toString());
            int exitValue = process.waitFor();
            if (exitValue == 0 )
                return true;
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(String.format("Cannot execute %s command", stringBuilder.toString()));
        } catch (InterruptedException exception) {
            exception.printStackTrace();
            throw new RuntimeException(String.format("Cannot execute %s command", stringBuilder.toString()));
        }
    }
}
