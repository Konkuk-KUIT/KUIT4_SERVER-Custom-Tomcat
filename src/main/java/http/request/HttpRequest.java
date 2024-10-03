package http.request;

import http.util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

public class HttpRequest {
    private final HttpRequestStartLine httpRequestStartLine;
    private final HttpRequestHeader httpRequestHeader;
    private final HttpRequestBody httpRequestBody;

    private HttpRequest(HttpRequestStartLine httpRequestStartLine, HttpRequestHeader httpRequestHeader,
                        HttpRequestBody httpRequestBody) {
        this.httpRequestStartLine = httpRequestStartLine;
        this.httpRequestHeader = httpRequestHeader;
        this.httpRequestBody = httpRequestBody;
    }

    public static HttpRequest from(BufferedReader br) throws IOException {

        HttpRequestStartLine httpRequestStartLine = HttpRequestStartLine.createHttpStartLine(br.readLine());
        HttpRequestHeader httpRequestHeader = HttpRequestHeader.createHttpHeader(br);
        String requestBodyLine = IOUtils.readData(br, httpRequestHeader.getContentLength());
        HttpRequestBody httpRequestBody = HttpRequestBody.CreateRequestBody(requestBodyLine);
        return new HttpRequest(httpRequestStartLine, httpRequestHeader, httpRequestBody);
    }

    public String getUrl() {
        return httpRequestStartLine.getUrl();
    }

    public String getMethod() {
        return httpRequestStartLine.getMethod();
    }

    public String getQueryString() {
        return httpRequestStartLine.getQueryString();
    }

    public String getCookie() {
        return httpRequestHeader.getCookie();
    }

    public Map<String, String> getQueryParams(){
        return httpRequestBody.getQueryParams();
    }
}
