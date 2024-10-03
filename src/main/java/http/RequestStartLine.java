package http;

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

    public static RequestStartLine from(String httpMethod, String url, Map<String, String> queryMap){
        return new RequestStartLine(httpMethod, url, queryMap);
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
