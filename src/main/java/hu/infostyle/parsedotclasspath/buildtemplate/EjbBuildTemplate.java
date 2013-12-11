package hu.infostyle.parsedotclasspath.buildtemplate;

import hu.infostyle.parsedotclasspath.antutil.AntExportable;
import hu.infostyle.parsedotclasspath.antutil.AntPropertyType;
import hu.infostyle.parsedotclasspath.antutil.AntUtils;
import org.apache.commons.io.FileUtils;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EjbBuildTemplate extends BaseTemplate implements AntExportable {
    protected HashMap<String, Object> classpathVarMap;

    public HashMap<String, Object> getClasspathVarMap() {
        return classpathVarMap;
    }

    public void setClasspathVarMap(HashMap<String, Object> classpathVarMap) {
        this.classpathVarMap = classpathVarMap;
    }

    public EjbBuildTemplate(String workspaceRootDir, String outputFilenameWithPath) {
        super(workspaceRootDir, outputFilenameWithPath);
        classpathVarMap = new HashMap<String, Object>();
    }

    @Override
    public void export() {
        StringWriter stringWriter = new StringWriter();
        XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
        try {
            xmlOutputter.output(buildFileContent, stringWriter);
            if (outputFile.exists()) {
                if (outputFile.delete()) {
                    System.out.println(outputFile.getAbsoluteFile().getName() + " deleted");
                }
                else {
                    System.out.println(outputFile.getAbsoluteFile().getName() + " not deleted");
                }
            }
            FileUtils.writeStringToFile(outputFile, stringWriter.toString(), true);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Can not export document to buildfile");
        }
    }

    public void init() {
		this.createBuildFileWithProjectElement();
        this.addPropertyElement(AntPropertyType.FILE, null, workspaceRootDir+"/gen_global.properties");
        this.addPropertyElement(AntPropertyType.NAME, "debuglevel", "source,lines,vars");
        this.addPropertyElement(AntPropertyType.NAME, "target", "1.6");
        this.addPropertyElement(AntPropertyType.NAME, "source", "1.6");
        this.addPropertyElement(AntPropertyType.NAME, "encoding", "UTF-8");
    }

    public void createBuildFileWithProjectElement() {
        Element projectElement = new Element(AntUtils.BUILD_PROJECT_ELEMENT);
        projectElement.setAttribute(AntUtils.BUILD_PROJECT_NAME_ATTR, outputFile.getParentFile().getName());
        projectElement.setAttribute(AntUtils.BUILD_PROJECT_DEF_ATTR, "build-project");
        projectElement.setAttribute(AntUtils.BUILD_PROJECT_BASEDIR_ATTR, ".");
        buildFileContent.setRootElement(projectElement);
    }

    public void addClasspathElement(String projectDirectory) {
        String classpathVariableName = new File(projectDirectory).getName();
        Element classpath = new Element(AntUtils.BUILD_PATH_ELEMENT);
        classpath.setAttribute(AntUtils.BUILD_PATH_ID_ATTR, classpathVariableName);
        Element pathElement = new Element(AntUtils.BUILD_PATHELEMENT);
        pathElement.setAttribute(AntUtils.BUILD_PATH_PATHELEMENT_PATH_ATTR, String.format("${%s}", classpathVariableName));
        classpath.addContent(pathElement);
        this.appendContentToBuildFile(buildFileContent, classpath);
    }

    public void addInitTarget(String classesDir, String sourceDir, List<String> excludesList, boolean includeEmptyDirs) {
        if (excludesList == null) {
            excludesList = new ArrayList<String>();
            excludesList.add("**/*.java");
        }
        Element target = new Element(AntUtils.BUILD_TARGET_ELEMENT);
        target.setAttribute(AntUtils.BUILD_TARGET_NAME_ATTR, AntUtils.BUILD_TARGET_NAME_INIT);
        Element mkdir = new Element(AntUtils.BUILD_MKDIR_ELEMENT);
        mkdir.setAttribute(AntUtils.BUILD_MKDIR_DIR_ATTR, classesDir);
        Element copy = new Element(AntUtils.BUILD_COPY_ELEMENT);
        copy.setAttribute(AntUtils.BUILD_COPY_TODIR_ATTR, classesDir);
        copy.setAttribute(AntUtils.BUILD_COPY_INCLEMPTYDIRS, Boolean.valueOf(includeEmptyDirs).toString());
        Element fileset = new Element(AntUtils.BUILD_FILESET_ELEMENT);
        fileset.setAttribute(AntUtils.BUILD_FILESET_DIR_ATTR, sourceDir);
        for(String exclude : excludesList) {
            Element excludeElement = new Element(AntUtils.BUILD_EXCLUDE_ELEMENT);
            excludeElement.setAttribute(AntUtils.BUILD_EXCLUDE_NAME_ATTR, exclude);
            fileset.addContent(excludeElement);
        }
        copy.addContent(fileset);
        target.addContent(mkdir);
        target.addContent(copy);
        this.appendContentToBuildFile(buildFileContent, target);
    }

    public void addCleanTarget(List<String> dirsToDelete) {
        Element target = new Element(AntUtils.BUILD_TARGET_ELEMENT);
        target.setAttribute(AntUtils.BUILD_TARGET_NAME_ATTR, AntUtils.BUILD_TARGET_NAME_CLEAN);
        Element delete = new Element(AntUtils.BUILD_DELETE_ELEMENT);
        for(String dirToDelete : dirsToDelete) {
            delete.setAttribute(AntUtils.BUILD_DELETE_DIR_ATTR, dirToDelete);
            target.addContent(delete);
        }
        this.appendContentToBuildFile(buildFileContent, target);
    }

    public void addCleanAllTarget() {
        Element target = new Element(AntUtils.BUILD_TARGET_ELEMENT);
        target.setAttribute(AntUtils.BUILD_TARGET_DEPENDS_ATTR, AntUtils.BUILD_TARGET_NAME_CLEAN);
        target.setAttribute(AntUtils.BUILD_TARGET_NAME_ATTR, AntUtils.BUILD_TARGET_NAME_CLEANALL);
        this.appendContentToBuildFile(buildFileContent, target);
    }

    public void addBuildProjectTarget(boolean debug, String destDir, String sourceDir, String classPath) {
        Element target = new Element(AntUtils.BUILD_TARGET_ELEMENT);
        target.setAttribute(AntUtils.BUILD_TARGET_DEPENDS_ATTR, AntUtils.BUILD_TARGET_NAME_INIT);
        target.setAttribute(AntUtils.BUILD_TARGET_NAME_ATTR, AntUtils.BUILD_TARGET_NAME_BUILDPROJECT);
        Element echo = new Element(AntUtils.BUILD_ECHO_ELEMENT);
        echo.setAttribute(AntUtils.BUILD_ECHO_MESSAGE_ATTR, AntUtils.BUILD_ECHO_MESSAGE_VALUE);
        target.addContent(echo);
        Element javac = new Element(AntUtils.BUILD_JAVAC_ELEMENT);
        javac.setAttribute(AntUtils.BUILD_JAVAC_DEBUG_ATTR, Boolean.valueOf(debug).toString());
        javac.setAttribute(AntUtils.BUILD_JAVAC_DEBUGLEVEL_ATTR, AntUtils.BUILD_JAVAC_DEBUGLEVEL_VALUE);
        javac.setAttribute(AntUtils.BUILD_JAVAC_DESTDIR_ATTR, destDir);
        javac.setAttribute(AntUtils.BUILD_JAVAC_SOURCE_ATTR, AntUtils.BUILD_JAVAC_SOURCE_VALUE);
        javac.setAttribute(AntUtils.BUILD_JAVAC_TARGET_ATTR, AntUtils.BUILD_JAVAC_TARGET_VALUE);
        javac.setAttribute(AntUtils.BUILD_JAVAC_ENCODING_ATTR, AntUtils.BUILD_JAVAC_ENCODING_VALUE);
        javac.addContent(new Element(AntUtils.BUILD_SRC_ELEMENT).setAttribute(AntUtils.BUILD_SRC_PATH_ATTR, sourceDir));
        javac.addContent(new Element(AntUtils.BUILD_CLASSPATH_ELEMENT).setAttribute(AntUtils.BUILD_CLASSPATH_REFID_ATTR, classPath));
        target.addContent(javac);
        this.appendContentToBuildFile(buildFileContent, target);
    }
}
