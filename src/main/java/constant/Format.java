package constant;

public enum Format {
    HTML("html"),
    CSS("css");

    private String format;

    Format(String format) {
        this.format = format;
    }

    public String getFormat() {
        return format;
    }
}
