package http.request;

import java.io.BufferedReader;

public class HttpRequest {
    private final HttpRequestStartLine startLine;
    private final String header;
    private final String body;

    public HttpRequest(HttpRequestStartLine startLine, String header, String body) {
        this.startLine = startLine;
        this.header = header;
        this.body = body;
    }

    public HttpRequestStartLine getStartLine() {
        return startLine;
    }

    public String getHeader() {
        return header;
    }

    public String getBody() {
        return body;
    }

    public static HttpRequest from(BufferedReader br){

    }
}
