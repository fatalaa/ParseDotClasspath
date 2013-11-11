package hu.infostyle.parsedotclasspath.antutils;

import java.io.File;

public class AntBuildWriter {
    private File outputFile;

    public File getOutputFilenameWithPath() {
        return outputFile;
    }

    public void setOutputFilenameWithPath(File file) {
        this.outputFile = file;
    }

    public AntBuildWriter(String outputFilenameWithPath) {
        this.outputFile = new File(outputFilenameWithPath);
    }

    public static void export(String filenameWithAbsoulutePath) {
    }

    public void writeProperty(AntPropertyType propertyType, String propertyValue) {
        SAXBuilder saxBuilder = new SAXBuilder(outputFile);
        try {

        }
    }
}
