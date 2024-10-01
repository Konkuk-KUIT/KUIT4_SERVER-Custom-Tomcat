package http.request;

import http.constant.HttpMethod;
import http.util.HttpRequestUtils;

import java.util.Map;

import static http.util.HttpRequestUtils.*;

public class HttpRequestStartLine {
    HttpMethod method;
    String target;
    String version;
    Map<String, String> queryString;

    public HttpRequestStartLine(HttpMethod method, String target, String version, Map<String, String> queryString) {
        this.method = method;
        this.target = target;
        this.version = version;
        this.queryString = queryString;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getTarget() {
        return target;
    }

    public String getVersion() {
        return version;
    }

    public Map<String, String> getQueryString() {
        return queryString;
    }

    public static HttpRequestStartLine from(String startLine){
        String[] startLines = startLine.split(" ");

        HttpMethod method = HttpMethod.valueOf(startLines[0]);
        String[] targetURL = startLines[1].split("\\?");
        String target = targetURL[0];
        Map<String, String> queryParameter = parseQueryParameter(targetURL[1]);
        String version = startLines[2];

        return new HttpRequestStartLine(method, target, version, queryParameter);
    }
}
