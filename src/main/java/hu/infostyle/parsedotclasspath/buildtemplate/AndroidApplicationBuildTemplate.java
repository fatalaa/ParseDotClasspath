package hu.infostyle.parsedotclasspath.buildtemplate;

import hu.infostyle.parsedotclasspath.antutil.AntExportable;
import hu.infostyle.parsedotclasspath.eclipseutil.EnvironmentVariables;

import java.io.IOException;

public class AndroidApplicationBuildTemplate extends BaseTemplate implements AntExportable {
    private String androidHome;

    public AndroidApplicationBuildTemplate(String workspaceRootDir, EnvironmentVariables environmentVariables, String outputFileWithPath) {
        super(workspaceRootDir, outputFileWithPath);
        if (environmentVariables != null) {
            androidHome = environmentVariables.getVariableByKey("ANDROID_HOME");
            return;
        }
        throw new RuntimeException("ANDROID_HOME is not set in Eclipse");
    }

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

    @Override
    public void export() {

    }
}
