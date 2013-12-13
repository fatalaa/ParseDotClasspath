package hu.infostyle.parsedotclasspath.buildtemplate;

import hu.infostyle.parsedotclasspath.antutil.AntPropertyType;
import hu.infostyle.parsedotclasspath.antutil.AntUtils;
import hu.infostyle.parsedotclasspath.antutil.PropertyFileOperator;
import org.javatuples.Triplet;
import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;

import java.io.File;
import java.util.List;

public abstract class BaseTemplate implements PropertyFileOperator {
    protected Document buildFileContent;
    protected File outputFile;
	protected String workspaceRootDir;

    protected BaseTemplate(String workspaceRootDir, String outputFilenameWithPath) {
        this.outputFile = new File(outputFilenameWithPath);
		this.workspaceRootDir = workspaceRootDir;
        this.buildFileContent = new Document();
    }

    public void createBuildFileWithProjectElement() {
        Element projectElement = new Element(AntUtils.BUILD_PROJECT_ELEMENT);
        projectElement.setAttribute(AntUtils.BUILD_PROJECT_NAME_ATTR, outputFile.getParentFile().getName());
        projectElement.setAttribute(AntUtils.BUILD_PROJECT_DEF_ATTR, "build-project");
        projectElement.setAttribute(AntUtils.BUILD_PROJECT_BASEDIR_ATTR, ".");
        buildFileContent.setRootElement(projectElement);
    }

    @Override
    public void addPropertyElement(AntPropertyType propertyType, String propertyKey, String propertyValue) {
        if (!buildFileContent.hasRootElement())
            throw new RuntimeException("Build file has no root element");
        if (propertyType.equals(AntPropertyType.FILE)) {
            Element propertyElement = new Element(AntUtils.BUILD_PROPERTY_ELEMENT);
            propertyElement.setAttribute(propertyType.getPropertyType(), propertyValue);
            this.appendContentToBuildFile(buildFileContent, propertyElement);
        } else {
            Element propertyElement = new Element(AntUtils.BUILD_PROPERTY_ELEMENT);
            propertyElement.setAttribute(propertyType.getPropertyType(), propertyKey);
            propertyElement.setAttribute(AntUtils.BUILD_PROPERTY_VALUE_ATTR, propertyValue);
            this.appendContentToBuildFile(buildFileContent, propertyElement);
        }
    }

    public void addBuildAllTarget(List<Triplet<String, String, String>> refprojects) {
        if (!buildFileContent.hasRootElement())
            throw new RuntimeException("Build file has no root element");
        Element buildDependencyProjectsElement = new Element(AntUtils.BUILD_TARGET_ELEMENT);
        buildDependencyProjectsElement.setAttribute("name", "build-all");
        for(Triplet<String, String, String> refProject : refprojects) {
            Element buildDependencyProjectElement = new Element("ant");
            buildDependencyProjectElement.setAttribute("antfile", "build.xml");
            buildDependencyProjectElement.setAttribute("dir", refProject.getValue1());
            buildDependencyProjectElement.setAttribute("inheritAll", "false");
            buildDependencyProjectElement.setAttribute("target", "clean");
            Element cleanDependencyProjectElement = new Element("ant");
            cleanDependencyProjectElement.setAttribute("antfile", "build.xml");
            cleanDependencyProjectElement.setAttribute("dir", refProject.getValue1());
            cleanDependencyProjectElement.setAttribute("inheritAll", "false");
            cleanDependencyProjectElement.setAttribute("target", "build-project");
            buildDependencyProjectsElement.addContent(buildDependencyProjectElement);
            buildDependencyProjectsElement.addContent(cleanDependencyProjectElement);
        }
        Element invokeActualBuildProjectElement = new Element("ant");
        invokeActualBuildProjectElement.setAttribute("antfile", "build.xml");
        invokeActualBuildProjectElement.setAttribute("dir", ".");
        invokeActualBuildProjectElement.setAttribute("inheritAll", "false");
        invokeActualBuildProjectElement.setAttribute("target", "build-project");
        buildDependencyProjectsElement.addContent(invokeActualBuildProjectElement);
        this.appendContentToBuildFile(buildFileContent, buildDependencyProjectsElement);
    }

    public String getProjectHome() {
        return this.outputFile.getParentFile().getAbsolutePath();
    }

    public String getProjectName() {
        return this.outputFile.getParentFile().getName();
    }

    protected void appendContentToBuildFile(Document buildFile, Content contentToAppend) {
        if (buildFile != null && contentToAppend != null)
            buildFile.getRootElement().addContent(contentToAppend);
        else
            throw new RuntimeException("Can not append node to buildfile");
    }
}
