package http.request;

import http.util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;

import static http.util.IOUtils.readData;

public class HttpRequest {
    private final HttpRequestStartLine httpRequestStartLine;
    private final HttpRequestHeader httpRequestHeader;
    private final String body;

    private HttpRequest(HttpRequestStartLine httpRequestStartLine, HttpRequestHeader httpRequestHeader, String body) {
        this.httpRequestStartLine = httpRequestStartLine;
        this.httpRequestHeader = httpRequestHeader;
        this.body = body;
    }

    public static HttpRequest from(BufferedReader br) throws IOException {
        //startLine 추출
        String startLine = br.readLine();

        HttpRequestStartLine requestStartLine = HttpRequestStartLine.from(startLine);
        HttpRequestHeader requestHeader = HttpRequestHeader.from(br);
        String body = readBody(br, requestHeader);

        return new HttpRequest(requestStartLine, requestHeader, body);
    }

    private static String readBody(BufferedReader br, HttpRequestHeader requestHeader) throws IOException {
        if (!requestHeader.isEmptyContent()) {
            return readData(br, requestHeader.getContentLength());
        }
        return "";
    }
}
