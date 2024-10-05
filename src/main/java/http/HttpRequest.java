package http;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private String method;
    private String path;
    private String version;
    private Map<String, String> headers = new HashMap<>();
    private String body;
    private String queryString;

    // 생성자: 필요한 필드를 설정
    public HttpRequest(String method, String path, String version, Map<String, String> headers, String body, String queryString) {
        this.method = method;
        this.path = path;
        this.version = version;
        this.headers = headers;
        this.body = body;
        this.queryString = queryString;
    }

    // Getter 메서드들
    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getVersion() {
        return version;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public String getQueryString() {
        return queryString;
    }
}
