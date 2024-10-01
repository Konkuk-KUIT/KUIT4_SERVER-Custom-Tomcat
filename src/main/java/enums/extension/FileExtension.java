package enums.extension;

public enum FileExtension {
    HTML("html"), CSS("css");

    private final String value;

    FileExtension(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String addFrontPoint() {
        return "."+value;
    }
}
