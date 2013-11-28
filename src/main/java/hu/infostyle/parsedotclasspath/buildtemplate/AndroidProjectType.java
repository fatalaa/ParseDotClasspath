package hu.infostyle.parsedotclasspath.buildtemplate;

public enum AndroidProjectType {
    LIBRARY("lib"),
    APPLICATION("app");

    private String projectType;

    private AndroidProjectType(String projectType) {
        this.projectType = projectType;
    }

    public String getProjectType() {
        return projectType;
    }
}