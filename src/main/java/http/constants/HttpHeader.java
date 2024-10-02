package http.constants;

import java.util.Arrays;
import java.util.Optional;

public enum HttpHeader {
    CONTENT_LENGTH("Content-Length"), CONTENT_TYPE("Content-Type"), LOCATION("Location"), COOKIE("Cookie"),SET_COOKIE("Set-Cookie"),CONNECTION("Connection"),HOST("Host");

    private final String header;

    HttpHeader(String header) {
        this.header = header;
    }

    public static HttpHeader getHeaderInstance(String headerString) {
        Optional<HttpHeader> find = Arrays.stream(HttpHeader.values()).filter(httpHeader -> httpHeader.header.equals(headerString)).findFirst();
        return find.orElse(null);
    }

    public String getHeader() {
        return header;
    }
}
