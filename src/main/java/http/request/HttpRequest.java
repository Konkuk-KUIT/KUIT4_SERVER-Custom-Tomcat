package http.request;

import java.io.BufferedReader;
import java.io.IOException;

public class HttpRequest {
    private final HttpRequestStartLine startLine;
    private final HttpRequestHeaders headers;
    private final HttpRequestBody body;

    private HttpRequest(HttpRequestStartLine startLine, HttpRequestHeaders headers, HttpRequestBody body) {
        this.startLine = startLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest from(BufferedReader br) throws IOException {
        String startLine = br.readLine();
        HttpRequestStartLine httpStartLine = HttpRequestStartLine.from(startLine);

        HttpRequestHeaders httpHeaders = HttpRequestHeaders.from(br);

        int contentLength = httpHeaders.getContentLength();
        HttpRequestBody httpBody = HttpRequestBody.from(br, contentLength);

        return new HttpRequest(httpStartLine, httpHeaders, httpBody);
    }

    public String getMethod() {
        return startLine.getMethod();
    }

    public String getPath() {
        return startLine.getPath();
    }

    public String getHeader(String name) {
        return headers.getHeader(name);
    }

    public String getBody() {
        return body.getBody();
    }
}
