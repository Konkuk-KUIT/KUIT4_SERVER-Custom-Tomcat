package webserver;

import http.util.HttpRequestUtils;
import http.util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private String method;
    private String path;
    private String version;
    private Map<String, String> headers;
    private Map<String, String> queryParams;
    private Map<String, String> bodyParams;

    // 생성자
    private HttpRequest(String method, String path, String version, Map<String, String> headers,
                        Map<String, String> queryParams, Map<String, String> bodyParams) {
        this.method = method;
        this.path = path;
        this.version = version;
        this.headers = headers;
        this.queryParams = queryParams;
        this.bodyParams = bodyParams;
    }

    // 정적 팩토리 메서드: BufferedReader를 통해 HttpRequest 객체 생성
    public static HttpRequest from(BufferedReader br) throws IOException {
        // 1. 요청 라인 파싱
        String requestLine = br.readLine();
        if (requestLine == null || requestLine.isEmpty()) {
            throw new IllegalArgumentException("Invalid request line");
        }
        String[] requestTokens = requestLine.split(" ");
        String method = requestTokens[0];
        String url = requestTokens[1];
        String protocol = requestTokens[2];

        // 2. 헤더 파싱
        Map<String, String> headers = new HashMap<>();
        String line;
        while (!(line = br.readLine()).isEmpty()) {
            String[] headerTokens = line.split(": ", 2);
            if (headerTokens.length == 2) {
                headers.put(headerTokens[0], headerTokens[1]);
            }
        }

        // 3. URL에서 경로와 쿼리 스트링 분리
        String path = url.split("\\?")[0];
        Map<String, String> queryParams = new HashMap<>();
        if (url.contains("?")) {
            String queryString = url.split("\\?")[1];
            queryParams = HttpRequestUtils.parseQueryParameter(queryString);
        }

        // 4. 바디 파싱 (Content-Length가 있을 때만)
        Map<String, String> bodyParams = new HashMap<>();
        if (headers.containsKey("Content-Length")) {
            int contentLength = Integer.parseInt(headers.get("Content-Length"));
            String bodyContent = IOUtils.readData(br, contentLength);
            bodyParams = HttpRequestUtils.parseQueryParameter(bodyContent);
        }

        return new HttpRequest(method, path, protocol, headers, queryParams, bodyParams);
    }

    // Getters
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

    public String getHeader(String key) {
        return headers.get(key);
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public Map<String, String> getBodyParams() {
        return bodyParams;
    }
}
