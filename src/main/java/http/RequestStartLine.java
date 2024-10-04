package http;

import http.util.HttpRequestUtils;

import java.util.HashMap;
import java.util.Map;

public class RequestStartLine {
    private String httpMethod;
    private String url;
    private Map<String, String> queryMap;

    private RequestStartLine(String httpMethod, String url, Map<String, String> queryMap) {
        this.httpMethod = httpMethod;
        this.url = url;
        this.queryMap = queryMap;
    }

    public static RequestStartLine from(String rawStartLine){
        String httpMethod = rawStartLine.split(" ")[0];
        String rawUrl = rawStartLine.split(" ")[1];
        String url = extractUrl(rawUrl);
        Map<String, String> queryMap = extractQueryParameters(rawUrl);
        return new RequestStartLine(httpMethod, url, queryMap);
    }

    private static String extractUrl(String rawUrl) {
        int questionMarkIndex = rawUrl.indexOf('?');
        String url = questionMarkIndex != -1 ? rawUrl.substring(0, questionMarkIndex) : rawUrl;

        if (url.endsWith("/")) url = url.substring(0, url.length() - 1);
        if (!url.startsWith("/")) url = "/" + url;

        return url;
    }

    private static Map<String, String> extractQueryParameters(String rawUrl) {
        int questionMarkIndex = rawUrl.indexOf('?');
        if (questionMarkIndex == -1) {
            return new HashMap<>();
        }
        String queryString = rawUrl.substring(questionMarkIndex + 1);
        return HttpRequestUtils.parseQueryParameter(queryString);
    }

    public String getUrl() {
        return url;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public Map<String, String> getQueryMap() {
        return queryMap;
    }
}
