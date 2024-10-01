package constant;

public enum HttpHeader {
    CONTENT_TYPE("Content-Type"),
    LOCATION("Location"),
    CONTENT_LENGTH("Content-Length"),
    HTTP_VERSION("HTTP/1.1"),
    COOKIE("Cookie"),
    SET_COOKIE("Set-Cookie"),
    ACCEPT("Accept")
    ;

    private final String header;

    HttpHeader(String header) {
        this.header = header;
    }

    public String getHeader() {
        return header;
    }
}
