package hu.infostyle.parsedotclasspath.buildtemplate;

import hu.infostyle.parsedotclasspath.antutil.AntUtils;
import org.apache.commons.io.FileUtils;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.IOException;
import java.io.StringWriter;

public class GwtBuildTemplate extends EjbBuildTemplate {
    public GwtBuildTemplate(String workspaceRootDir, String outputFilenameWithPath) {
        super(workspaceRootDir, outputFilenameWithPath);
    }

    public void AddGwtCompileTarget(String classpathVariable, String gwtModuleName) {
        Element target = new Element(AntUtils.BUILD_TARGET_ELEMENT);
        target.setAttribute(AntUtils.BUILD_TARGET_NAME_ATTR, AntUtils.BUILD_GWT_TARGET_NAME_VALUE);
        target.setAttribute(AntUtils.BUILD_TARGET_DEPENDS_ATTR, AntUtils.BUILD_TARGET_NAME_BUILDPROJECT);
        Element java = new Element(AntUtils.BUILD_GWT_JAVA_ELEMENT);
        java.setAttribute(AntUtils.BUILD_GWT_JAVA_FAILONERROR_ATTR, Boolean.valueOf(true).toString());
        java.setAttribute(AntUtils.BUILD_GWT_JAVA_FORK_ATTR, Boolean.valueOf(true).toString());
        java.setAttribute(AntUtils.BUILD_GWT_JAVA_CLASSNAME_ATTR, AntUtils.BUILD_GWT_JAVA_CLASSNAME_VALUE);
        Element javaClasspath = new Element(AntUtils.BUILD_CLASSPATH_ELEMENT);
        Element javaClasspathElement = new Element(AntUtils.BUILD_PATHELEMENT);
        javaClasspathElement.setAttribute(AntUtils.BUILD_PATH_ELEMENT, classpathVariable);
        javaClasspath.addContent(javaClasspathElement);
        Element jvmarg = new Element(AntUtils.BUILD_GWT_JAVA_JVMARG_ELEMENT);
        jvmarg.setAttribute(AntUtils.BUILD_COMMON_VALUE_ATTR, AntUtils.BUILD_GWT_JVMARG_MEMORY_VALUE);
        java.addContent(jvmarg);
        Element wararg = new Element(AntUtils.BUILD_GWT_JAVA_ARG_ELEMENT);
        wararg.setAttribute(AntUtils.BUILD_GWT_JAVA_ARG_LINE_ATTR, AntUtils.BUILD_GWT_WARARG_LINE);
        java.addContent(wararg);
        Element warargValue = new Element(AntUtils.BUILD_GWT_JAVA_ARG_ELEMENT);
        warargValue.setAttribute(AntUtils.BUILD_COMMON_VALUE_ATTR, AntUtils.BUILD_GWT_WARARG_VALUE);
        java.addContent(warargValue);
        Element gwtCmdLineArgs = new Element(AntUtils.BUILD_GWT_JAVA_ARG_ELEMENT);
        gwtCmdLineArgs.setAttribute(AntUtils.BUILD_GWT_JAVA_ARG_LINE_ATTR, AntUtils.BUILD_GWT_GWTARGVARS_VALUE);
        java.addContent(gwtCmdLineArgs);
        Element gwtModule = new Element(AntUtils.BUILD_GWT_JAVA_ARG_ELEMENT);
        gwtModule.setAttribute(AntUtils.BUILD_COMMON_VALUE_ATTR, gwtModuleName);
        java.addContent(gwtModule);
        target.addContent(java);
        this.appendContentToBuildFile(this.buildFileContent, target);
    }

    @Override
    public void export() {
        super.export();
        StringWriter stringWriter = new StringWriter();
        XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
        try {
            xmlOutputter.output(buildFileContent, stringWriter);
            FileUtils.writeStringToFile(outputFile, stringWriter.toString());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Can not export GWT specific content to buildfile");
        }
    }
}
