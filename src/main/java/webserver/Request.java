package webserver;

import http.util.HttpRequestUtils;
import http.util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Request {
    private String method;
    private String path;
    private Map<String, String> headers;
    private Map<String, String> queryParams;
    private Map<String, String> bodyParams;


    private Request(String method, String path, Map<String, String> headers,
                    Map<String, String> queryParams, Map<String, String> bodyParams) {
        this.method = method;
        this.path = path;
        this.headers = headers;
        this.queryParams = queryParams;
        this.bodyParams = bodyParams;
    }


    public static Request from(BufferedReader br) throws IOException {

        String requestLine = br.readLine();
        if (requestLine == null || requestLine.isEmpty()) {
            throw new IllegalArgumentException("Invalid request line");
        }
        String[] requestTokens = requestLine.split(" ");
        String method = requestTokens[0];
        String url = requestTokens[1];

        // 헤더 파싱
        Map<String, String> headers = parseHeaders(br);

        String path = parsePath(url);
        Map<String, String> queryParams = parseQuery(url);

        // POST: 바디 파라미터 파싱
        Map<String, String> bodyParams = parseBody(br, headers);

        return new Request(method, path, headers, queryParams, bodyParams);
    }

    // 헤더 파싱 메서드
    private static Map<String, String> parseHeaders(BufferedReader br) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String line;
        while (!(line = br.readLine()).isEmpty()) {
            String[] headerTokens = line.split(": ", 2);
            if (headerTokens.length == 2) {
                headers.put(headerTokens[0], headerTokens[1]);
            }
        }
        return headers;
    }

    // URL에서 경로만 추출
    private static String parsePath(String url) {
        return url.split("\\?")[0];
    }

    // URL에서 쿼리 파라미터 추출
    private static Map<String, String> parseQuery(String url) {
        Map<String, String> queryParams = new HashMap<>();
        if (url.contains("?")) {
            String queryString = url.split("\\?")[1];
            queryParams = HttpRequestUtils.parseQueryParameter(queryString);
        }
        return queryParams;
    }

    // POST: 바디 파라미터 파싱
    private static Map<String, String> parseBody(BufferedReader br, Map<String, String> headers) throws IOException {
        Map<String, String> bodyParams = new HashMap<>();
        if (headers.containsKey(HttpHeader.CONTENT_LENGTH.getHeaderValue())) {
            int contentLength = Integer.parseInt(headers.get(HttpHeader.CONTENT_LENGTH.getHeaderValue()));
            String bodyContent = IOUtils.readData(br, contentLength);
            bodyParams = HttpRequestUtils.parseQueryParameter(bodyContent);
        }
        return bodyParams;
    }



    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }


    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public Map<String, String> getBodyParams() {
        return bodyParams;
    }
}
