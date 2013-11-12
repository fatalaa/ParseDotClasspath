package hu.infostyle.parsedotclasspath.antutils;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class AntBuildWriter {
    private String filePathAndName;
    private File outputFile;

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

    public AntBuildWriter(String outputFilenameWithPath) {
        this.filePathAndName = outputFilenameWithPath;
    }

    public static void export(String filenameWithAbsoulutePath) {
    }

    public void createBuildFileWithProjectElement() {
        this.outputFile = new File(filePathAndName);
        SAXBuilder saxBuilder = new SAXBuilder();
        try {
            Document buildFile = saxBuilder.build(outputFile);
            Element projectElement = new Element(AntUtils.BUILD_PROJECT_ELEMENT);
            projectElement.setAttribute(AntUtils.BUILD_PROJECT_NAME_ATTR, outputFile.getParentFile().getName());
            projectElement.setAttribute(AntUtils.BUILD_PROJECT_DEF_ATTR, "build");
            projectElement.setAttribute(AntUtils.BUILD_PROJECT_BASEDIR_ATTR, ".");
            buildFile.setRootElement(projectElement);
        } catch (JDOMException exception) {
            exception.printStackTrace();
            throw new RuntimeException("Can't create document from file");
        } catch (IOException exception) {
            exception.printStackTrace();
            throw new RuntimeException("Can't create document from file");
        }
    }

    public void addPropertyElement(AntPropertyType propertyType, String propertyValue) {
        SAXBuilder saxBuilder = new SAXBuilder();
        try {
            Document buildFile = saxBuilder.build(outputFile);
            if (!buildFile.hasRootElement()) {
                this.createBuildFileWithProjectElement();
            }
            Element rootElement = buildFile.getRootElement();
            Element propertyElement = new Element(AntUtils.BUILD_PROPERTY_ELEMENT);
            propertyElement.setAttribute(propertyType.getPropertyType(), propertyValue);
            this.appendContentToBuildFile(buildFile, rootElement.addContent(propertyElement));
        } catch (JDOMException exception) {
        exception.printStackTrace();
        throw new RuntimeException("Can't create document from file");
        } catch (IOException exception) {
        exception.printStackTrace();
        throw new RuntimeException("Can't create document from file");
        }
    }

    public void addClasspathElement(String classpathValue) {
        SAXBuilder saxBuilder = new SAXBuilder();
        try {
            Document buildFile = saxBuilder.build(outputFile);
            Element rootElement = buildFile.getRootElement();
            Element classpath = new Element(AntUtils.BUILD_PATH_ELEMENT);
            classpath.setAttribute(AntUtils.BUILD_PATH_ID_ATTR, classpathValue);
            Element pathElement = new Element(AntUtils.BUILD_PATHELEMENT);
            pathElement.setAttribute(AntUtils.BUILD_PATH_PATHELEMENT_PATH_ATTR, classpathValue);
            classpath.addContent(pathElement);
            this.appendContentToBuildFile(buildFile, rootElement.addContent(classpath));
        } catch (JDOMException exception) {
            exception.printStackTrace();
            throw new RuntimeException("Can't create document from file");
        } catch (IOException exception) {
            exception.printStackTrace();
            throw new RuntimeException("Can't create document from file");
        }
    }

    public void addInitTarget(String classesDir, String sourceDir, List<String> excludesList, boolean includeEmptyDirs) {
        SAXBuilder saxBuilder = new SAXBuilder();
        try {
            Document buildFile = saxBuilder.build(outputFile);
            Element rootElement = buildFile.getRootElement();
            Element target = new Element(AntUtils.BUILD_TARGET_ELEMENT);
            target.setAttribute(AntUtils.BUILD_TARGET_NAME_ATTR, AntUtils.BUILD_TARGET_NAME_INIT);
            Element mkdir = new Element(AntUtils.BUILD_MKDIR_ELEMENT);
            mkdir.setAttribute(AntUtils.BUILD_MKDIR_DIR_ATTR, classesDir);
            Element copy = new Element(AntUtils.BUILD_COPY_ELEMENT);
            copy.setAttribute(AntUtils.BUILD_COPY_TODIR_ATTR, classesDir);
            copy.setAttribute(AntUtils.BUILD_COPY_INCLEMPTYDIRS, Boolean.valueOf(includeEmptyDirs).toString());
            Element fileset = new Element(AntUtils.BUILD_FILESET_ELEMENT);
            fileset.setAttribute(AntUtils.BUILD_FILESET_DIR_ATTR, sourceDir);
            Element excludeElement = new Element(AntUtils.BUILD_EXCLUDE_ELEMENT);
            for(String exclude : excludesList) {
                excludeElement.setAttribute(AntUtils.BUILD_EXCLUDE_NAME_ATTR, exclude);
                fileset.addContent(excludeElement);
            }
            copy.addContent(fileset);
            target.addContent(mkdir);
            target.addContent(copy);
            this.appendContentToBuildFile(buildFile, rootElement.addContent(target));
        } catch (JDOMException exception) {
            exception.printStackTrace();
            throw new RuntimeException("Can't create document from file");
        } catch (IOException exception) {
            exception.printStackTrace();
            throw new RuntimeException("Can't create document from file");
        }
    }

    public void addCleanTarget(List<String> dirsToDelete) {
        SAXBuilder saxBuilder = new SAXBuilder();
        try {
            Document buildFile = saxBuilder.build(outputFile);
            Element rootElement = buildFile.getRootElement();
            Element target = new Element(AntUtils.BUILD_TARGET_ELEMENT);
            target.setAttribute(AntUtils.BUILD_TARGET_NAME_ATTR, AntUtils.BUILD_TARGET_NAME_CLEAN);
            Element delete = new Element(AntUtils.BUILD_DELETE_ELEMENT);
            for(String dirToDelete : dirsToDelete) {
                delete.setAttribute(AntUtils.BUILD_DELETE_DIR_ATTR, dirToDelete);
                target.addContent(delete);
            }
            this.appendContentToBuildFile(buildFile, rootElement.addContent(target));
        } catch (JDOMException exception) {
            exception.printStackTrace();
            throw new RuntimeException("Can't create document from file");
        } catch (IOException exception) {
            exception.printStackTrace();
            throw new RuntimeException("Can't create document from file");
        }
    }

    public void addCleanAllTarget() {
        SAXBuilder saxBuilder = new SAXBuilder();
        try {
            Document buildFile = saxBuilder.build(outputFile);
            Element rootElement = buildFile.getRootElement();
            Element target = new Element(AntUtils.BUILD_TARGET_ELEMENT);
            target.setAttribute(AntUtils.BUILD_CLEANALL_DEPENDS_ATTR, AntUtils.BUILD_TARGET_NAME_CLEANALL);
            target.setAttribute(AntUtils.BUILD_TARGET_NAME_ATTR, AntUtils.BUILD_TARGET_NAME_CLEAN);
            this.appendContentToBuildFile(buildFile, rootElement.addContent(target));
        } catch (JDOMException exception) {
            exception.printStackTrace();
            throw new RuntimeException("Can't create document from file");
        } catch (IOException exception) {
            exception.printStackTrace();
            throw new RuntimeException("Can't create document from file");
        }
    }

    public void addBuildTarget() {

    }

    private void appendContentToBuildFile(Document buildFile, Element contentToAppend) {
        if (buildFile != null && contentToAppend != null)
            buildFile.setRootElement(buildFile.getRootElement().setContent(contentToAppend));
        else
            throw new RuntimeException("Can not append node to buildfile");
    }
}
