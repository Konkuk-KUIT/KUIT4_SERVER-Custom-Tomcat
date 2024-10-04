package enums.http.header;

public enum EntityHeader {
    CONTENTTYPE("Content-Type"),
    CONTENTLENGTH("Content-Length");

    private final String value;

    EntityHeader(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String toResponseString() {
        return value + ": ";
    }
}
