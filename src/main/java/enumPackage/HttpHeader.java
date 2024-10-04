package enumPackage;

public enum HttpHeader {
    HTTP_VERSION("HTTP/1.1"),
    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    LOCATION("Location"),
    COOKIE("Cookie"),
    SET_COOKIE("Set-Cookie"),
    ACCEPT("Accept"),
    TEXT_HTML("text/html;charset=utf-8"),
    TEXT_CSS("text/css;charset=utf-8");

    private final String header;
    HttpHeader(String header) {
        this.header = header;
    }
    public String getHeader() {
        return header;
    }
}
