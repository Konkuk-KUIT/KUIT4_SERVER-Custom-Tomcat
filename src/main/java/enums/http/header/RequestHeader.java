package enums.http.header;

public enum RequestHeader {
    COOKIE("Cookie");

    private final String value;

    RequestHeader(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
