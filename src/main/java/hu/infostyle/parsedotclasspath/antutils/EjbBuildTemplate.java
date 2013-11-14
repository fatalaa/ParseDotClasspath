package hu.infostyle.parsedotclasspath.antutils;

import org.apache.commons.io.FileUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

public class EjbBuildTemplate implements AntExportable {
    private String filePathAndName;
    private File outputFile;
    private Document buildFileContent;

    public String getFilePathAndName() {
        return filePathAndName;
    }

    public void setFilePathAndName(String filePathAndName) {
        this.filePathAndName = filePathAndName;
    }

    public File getOutputFilenameWithPath() {
        return outputFile;
    }

    public void setOutputFilenameWithPath(File file) {
        this.outputFile = file;
    }

    public Document getBuildFileContent() {
        return buildFileContent;
    }

    public void setBuildFileContent(Document buildFileContent) {
        this.buildFileContent = buildFileContent;
    }

    public EjbBuildTemplate(String outputFilenameWithPath) {
        this.filePathAndName = outputFilenameWithPath;
        this.outputFile = new File(this.filePathAndName);
        this.buildFileContent = new Document();
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

    public void createBuildFileWithProjectElement() {
        Element projectElement = new Element(AntUtils.BUILD_PROJECT_ELEMENT);
        projectElement.setAttribute(AntUtils.BUILD_PROJECT_NAME_ATTR, outputFile.getParentFile().getName());
        projectElement.setAttribute(AntUtils.BUILD_PROJECT_DEF_ATTR, "build-project");
        projectElement.setAttribute(AntUtils.BUILD_PROJECT_BASEDIR_ATTR, ".");
        buildFileContent.setRootElement(projectElement);
    }

    public void addPropertyFileElement(AntPropertyType propertyType, String propertyValue) {
        if (!buildFileContent.hasRootElement()) {
            this.createBuildFileWithProjectElement();
        }
        Element propertyElement = new Element(AntUtils.BUILD_PROPERTY_ELEMENT);
        propertyElement.setAttribute(propertyType.getPropertyType(), propertyValue);
        this.appendContentToBuildFile(buildFileContent, propertyElement);
    }

    public void addPropertyNameElement(AntPropertyType propertyType, String key, String value) {
        if (!buildFileContent.hasRootElement()) {
            this.createBuildFileWithProjectElement();
        }
        Element propertyElement = new Element(AntUtils.BUILD_PROPERTY_ELEMENT);
        propertyElement.setAttribute(propertyType.getPropertyType(), key);
        propertyElement.setAttribute(AntUtils.BUILD_PROPERTY_VALUE_ATTR, value);
        this.appendContentToBuildFile(buildFileContent, propertyElement);
    }

    public void addClasspathElement(String classpathValue) {
        Element classpath = new Element(AntUtils.BUILD_PATH_ELEMENT);
        classpath.setAttribute(AntUtils.BUILD_PATH_ID_ATTR, classpathValue);
        Element pathElement = new Element(AntUtils.BUILD_PATHELEMENT);
        pathElement.setAttribute(AntUtils.BUILD_PATH_PATHELEMENT_PATH_ATTR, String.format("${%s}", classpathValue));
        classpath.addContent(pathElement);
        this.appendContentToBuildFile(buildFileContent, classpath);
    }

    public void addInitTarget(String classesDir, String sourceDir, List<String> excludesList, boolean includeEmptyDirs) {
        Element target = new Element(AntUtils.BUILD_TARGET_ELEMENT);
        target.setAttribute(AntUtils.BUILD_TARGET_NAME_ATTR, AntUtils.BUILD_TARGET_NAME_INIT);
        Element mkdir = new Element(AntUtils.BUILD_MKDIR_ELEMENT);
        mkdir.setAttribute(AntUtils.BUILD_MKDIR_DIR_ATTR, classesDir);
        Element copy = new Element(AntUtils.BUILD_COPY_ELEMENT);
        copy.setAttribute(AntUtils.BUILD_COPY_TODIR_ATTR, classesDir);
        copy.setAttribute(AntUtils.BUILD_COPY_INCLEMPTYDIRS, Boolean.valueOf(includeEmptyDirs).toString());
        Element fileset = new Element(AntUtils.BUILD_FILESET_ELEMENT);
        fileset.setAttribute(AntUtils.BUILD_FILESET_DIR_ATTR, sourceDir);
        if (excludesList != null) {
            Element excludeElement = new Element(AntUtils.BUILD_EXCLUDE_ELEMENT);
            for(String exclude : excludesList) {
                excludeElement.setAttribute(AntUtils.BUILD_EXCLUDE_NAME_ATTR, exclude);
                fileset.addContent(excludeElement);
            }
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
        target.setAttribute(AntUtils.BUILD_CLEANALL_DEPENDS_ATTR, AntUtils.BUILD_TARGET_NAME_CLEAN);
        target.setAttribute(AntUtils.BUILD_TARGET_NAME_ATTR, AntUtils.BUILD_TARGET_NAME_CLEANALL);
        this.appendContentToBuildFile(buildFileContent, target);
    }

    public void addBuildProjectTarget(boolean debug, String destDir, String sourceDir, String classPath) {
        Element target = new Element(AntUtils.BUILD_TARGET_ELEMENT);
        target.setAttribute(AntUtils.BUILD_CLEANALL_DEPENDS_ATTR, AntUtils.BUILD_TARGET_NAME_INIT);
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

    private void appendContentToBuildFile(Document buildFile, Element contentToAppend) {
        if (buildFile != null && contentToAppend != null)
            buildFile.getRootElement().addContent(contentToAppend);
        else
            throw new RuntimeException("Can not append node to buildfile");
    }
}
