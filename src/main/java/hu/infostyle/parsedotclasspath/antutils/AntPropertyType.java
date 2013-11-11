package hu.infostyle.parsedotclasspath.antutils;

public enum AntPropertyType {
    NAME("name"),
    FILE("file");

    private String propertyType;

    private AntPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public String getPropertyType() {
        return propertyType;
    }
}
