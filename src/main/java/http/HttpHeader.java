package http;

public enum HttpHeader {

    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    LOCATION("Location"),
    SET_COOKIE("Set-Cookie"),
    COOKIE("Cookie"),
    LOGINED_TRUE("logined=true"),
    LOGINED_FALSE("logined=false");

    private final String value;

    HttpHeader(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
