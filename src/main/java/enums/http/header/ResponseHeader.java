package enums.http.header;

public enum ResponseHeader {
    LOCATION("Location"),
    SETCOOKIE("Set-Cookie");

    private final String value;

    ResponseHeader(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String toResponseString() {
        return value + ": ";
    }
}
