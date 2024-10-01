package http.constant;

public enum HttpHeader {
    CONTENT_LENGTH("Content-Length"),
    CONTENT_TYPE("Content-Type"),
    LOCATION("Location"),
    COOKIE("Cookie"),
    SET_COOKIE("Set-Cookie"),
    CONNECTION("Connection"),
    HOST("Host");


    private String headerName;

    HttpHeader(String headerName) {
        this.headerName = headerName;
    }

    public String getHeaderName() {
        return headerName;
    }
}
