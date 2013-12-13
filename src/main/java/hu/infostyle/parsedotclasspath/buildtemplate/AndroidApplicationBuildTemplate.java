package hu.infostyle.parsedotclasspath.buildtemplate;

import hu.infostyle.parsedotclasspath.antutil.AntExportable;
import hu.infostyle.parsedotclasspath.eclipseutil.EnvironmentVariables;
import hu.infostyle.parsedotclasspath.propertyfileutil.PropertyUtil;
import org.apache.commons.io.FileUtils;
import org.javatuples.Triplet;
import org.jdom2.Element;
import org.jdom2.filter.Filters;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
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
        int idx = 0;
        if (refProjects.size() > 0) {
            List<Element> deletes = new ArrayList<Element>();
            XPathExpression<Element> expression = XPathFactory.instance().compile("//import", Filters.element());
            for(Element element : expression.evaluate(buildFileContent)) {
                if (element.getAttributes().size() > 1 )
                    idx = buildFileContent.getRootElement().indexOf(element);
            }
            Element preBuildTarget = new Element("target").setAttribute("name", "-pre-build");
            for(Triplet<String, String, String> refProject : refProjects) {
                Element buildRefProjectElement = new Element("ant").setAttribute("antfile", "build.xml")
                        .setAttribute("dir", refProject.getValue1()).setAttribute("inheritAll", "false").setAttribute("target", "clean");
                Element cleanRefProjectElement = new Element("ant").setAttribute("antfile", "build.xml")
                        .setAttribute("dir", refProject.getValue1()).setAttribute("inheritAll", "false").setAttribute("target", "jar");
                String jarname = refProject.getValue1().substring(refProject.getValue1().lastIndexOf('/') + 1) + ".jar";
                Element copyRefProjectJarElement = new Element("copy").setAttribute("todir", "libs").setAttribute("file", refProject.getValue1() + "/" + jarname);
                Element deleteElement = new Element("delete").setAttribute("file", refProject.getValue1() + "/" + jarname);
                Element deleteFromLibsElement = new Element("delete").setAttribute("file", "libs/" + jarname);
                deletes.add(deleteFromLibsElement);
                deletes.add(deleteElement);
                preBuildTarget.addContent(buildRefProjectElement);
                preBuildTarget.addContent(cleanRefProjectElement);
                preBuildTarget.addContent(copyRefProjectJarElement);
            }
            buildFileContent.getRootElement().addContent(idx - 1, preBuildTarget);

            Element preCleanElement = new Element("target").setAttribute("name", "-pre-clean");
            preCleanElement.addContent(deletes);
            buildFileContent.getRootElement().addContent(idx - 1, preCleanElement);
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
