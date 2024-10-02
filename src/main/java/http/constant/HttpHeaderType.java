package http.constant;

import java.util.HashMap;
import java.util.Map;

public enum HttpHeaderType {
    CONTENT_LENGTH("Content-Length"),
    CONTENT_TYPE("Content-Type"),
    LOCATION("Location"),
    COOKIE("Cookie"),
    SET_COOKIE("Set-Cookie"),
    CONNECTION("Connection"),
    ACCEPT("Accept"),
    HOST("Host");


    private String headerType;
    private static final Map<String, HttpHeaderType> headerMap = new HashMap<>();
    static {
        for (HttpHeaderType header : HttpHeaderType.values()) {
            headerMap.put(header.getHeaderType(), header);
        }
    }

    HttpHeaderType(String headerType) {
        this.headerType = headerType;

    }

    public String getHeaderType() {
        return headerType;
    }

    public static HttpHeaderType getInstance(String headerName) {
        return headerMap.get(headerName);
    }
}
