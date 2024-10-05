package webserver;

public enum HttpHeader {
    CONTENT_LENGTH("Content-Length"),
    LOCATION("Location"),
    COOKIE("Cookie"),
    CONTENT_TYPE("Content-Type"),
    CONNECTION("Connection"),
    HTTP_302("HTTP/1.1 302 Found");

    private final String headerValue;

    HttpHeader(String headerValue) {
        this.headerValue = headerValue;
    }

    public String getHeaderValue() {
        return headerValue;
    }
}
