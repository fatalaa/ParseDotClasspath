package hu.infostyle.parsedotclasspath.antutil;

public class AntBuildWriter {

    public void export(AntExportable exportable) {
        if (exportable != null)
            exportable.export();
        else
            throw new RuntimeException("Cannot export AntExportable object");
    }
}