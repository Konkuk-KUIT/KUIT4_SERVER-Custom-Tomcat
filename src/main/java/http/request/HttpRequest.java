package http.request;

import http.util.HttpRequestUtils;
import http.util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

import static constant.HttpMethod.GET;
import static constant.HttpMethod.POST;
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

    public boolean isGetMethod() {
        return GET.isEqual(httpRequestStartLine.getMethod());
    }

    public boolean isPostMethod() {
        return POST.isEqual(httpRequestStartLine.getMethod());
    }

    public String getUrl() {
        return httpRequestStartLine.getUrl();
    }

    public int getContentLength() {
        return httpRequestHeader.getContentLength();
    }

    public boolean isLogin() {
        return httpRequestHeader.getLoginStatus();
    }

    public Map<String, String> getQueryParametersfromBody() {
        //POST 메서드인 경우
        return HttpRequestUtils.parseQueryParameter(body);
    }
    public Map<String, String> getQueryParametersfromUrl() {
        //GET 메서드인 경우
        String url = getUrl();
        String queryString = url.substring(url.lastIndexOf("?") + 1);
        return HttpRequestUtils.parseQueryParameter(queryString);
    }

}
