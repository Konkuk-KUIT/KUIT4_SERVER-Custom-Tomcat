package webserver.enums;

public enum HttpHeader {
    RESPONSE_OK("HTTP/1.1 200 OK"),
    RESPONSE_FOUND("HTTP/1.1 302 Found"),
    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    LOCATION("Location"),
    SET_COOKIE("Set-Cookie"),
    COOKIE("Cookie");

    private final String value;

    HttpHeader(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
