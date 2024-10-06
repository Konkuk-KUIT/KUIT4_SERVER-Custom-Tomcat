package Enum;

public enum HttpHeaderField {

    CONTENT_LENGTH("Content-Length: "),
    COOKIE("Cookie: "),
    LOCATION("Location: "),
    SET_COOKIE("Set-Cookie: "),
    CONTENT_TYPE("Content-Type: "),
    ;


    private final String httpHeader;

    HttpHeaderField(String httpHeader) {
        this.httpHeader = httpHeader;
    }

    public String getHttpHeader() {
        return httpHeader;
    }
}
