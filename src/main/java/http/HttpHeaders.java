package http;

public enum HttpHeaders {
    CONTENT_LENGTH("Content-Length"),
    COOKIE("Cookie"),
    SET_COOKIE("Set-Cookie"),
    LOCATION("Location"),
    CONTENT_TYPE("Content-Type");

    private final String header;

    HttpHeaders(String header) {
        this.header = header;
    }

    public String getHeader() {
        return header;
    }
}

