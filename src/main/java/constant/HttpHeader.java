package constant;

public enum HttpHeader {
    CONTENT_TYPE("Content-Type"),
    LOCATION("Location"),
    CONTENT_LENGTH("Content-Length"),
    COOKIE("Cookie"),
    SET_COOKIE("Set-Cookie")
    ;

    private final String header;

    HttpHeader(String header) {
        this.header = header;
    }

    public String getHeader() {
        return header;
    }
}
