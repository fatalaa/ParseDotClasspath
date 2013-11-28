package hu.infostyle.parsedotclasspath.buildtemplate;

import hu.infostyle.parsedotclasspath.antutil.AntPropertyType;
import hu.infostyle.parsedotclasspath.antutil.AntUtils;
import hu.infostyle.parsedotclasspath.antutil.PropertyFileOperator;
import org.jdom2.Document;
import org.jdom2.Element;

import java.io.File;

public abstract class BaseTemplate implements PropertyFileOperator {
    protected Document buildFileContent;
    protected File outputFile;

    protected BaseTemplate(String outputFilenameWithPath) {
        this.outputFile = new File(outputFilenameWithPath);
        this.buildFileContent = new Document();
    }

    public Document getBuildFileContent() {
        return buildFileContent;
    }

    public void setBuildFileContent(Document buildFileContent) {
        this.buildFileContent = buildFileContent;
    }

    public File getOutputFilenameWithPath() {
        return outputFile;
    }

    public void setOutputFilenameWithPath(File file) {
        this.outputFile = file;
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

    public String getProjectHome() {
        return this.outputFile.getParentFile().getAbsolutePath();
    }

    protected void appendContentToBuildFile(Document buildFile, Element contentToAppend) {
        if (buildFile != null && contentToAppend != null)
            buildFile.getRootElement().addContent(contentToAppend);
        else
            throw new RuntimeException("Can not append node to buildfile");
    }
}
