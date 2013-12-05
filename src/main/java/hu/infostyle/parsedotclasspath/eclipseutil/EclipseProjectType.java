package hu.infostyle.parsedotclasspath.eclipseutil;

public enum EclipseProjectType {
    EJB("EJB"),
    GWT("GWT"),
    ANDROID("ANDROID"),
    UNKNOWN("UNKNOWN");

    private String projectType;

    private EclipseProjectType(String projectType) {
        this.projectType = projectType;
    }

    public String getProjectType() {
        return projectType;
    }
}