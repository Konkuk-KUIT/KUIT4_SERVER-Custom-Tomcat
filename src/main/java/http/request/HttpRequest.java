package http;

import http.util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

public class HttpRequest {
    private final HttpStartLine httpStartLine;
    private final HttpHeader httpHeader;
    private final RequestBody requestBody;

    private HttpRequest(HttpStartLine httpStartLine, HttpHeader httpHeader,
                        http.RequestBody requestBody) {
        this.httpStartLine = httpStartLine;
        this.httpHeader = httpHeader;
        this.requestBody = requestBody;
    }

    public static HttpRequest from(BufferedReader br) throws IOException {

        HttpStartLine httpStartLine = HttpStartLine.createHttpStartLine(br.readLine());
        HttpHeader httpHeader = HttpHeader.createHttpHeader(br);
        String requestBodyLine = IOUtils.readData(br,httpHeader.getContentLength());
        RequestBody requestBody= http.RequestBody.CreateRequestBody(requestBodyLine);
        return new HttpRequest(httpStartLine,httpHeader,requestBody);
    }

    public String getUrl() {
        return httpStartLine.getUrl();
    }

    public String getMethod() {
        return httpStartLine.getMethod();
    }

    public String getQueryString() {
        return httpStartLine.getQueryString();
    }

    public String getCookie() {
        return httpHeader.getCookie();
    }

    public Map<String, String> getQueryParams(){
        return requestBody.getQueryParams();
    }
}
