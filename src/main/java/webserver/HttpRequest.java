package webserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static webserver.enums.HttpMethod.HTTP_POST;

public class HttpRequest {
    private String method;
    private String path;
    private Map<String, String> headers;
    private String body;

    public HttpRequest(BufferedReader br) throws IOException {
        headers = new HashMap<>();
        parseRequest(br);
    }

    private void parseRequest(BufferedReader br) throws IOException {
        String requestLine = br.readLine();
        String[] requestParts = requestLine.split(" ");
        method = requestParts[0];
        path = requestParts[1];

        String line;
        while (!(line = br.readLine()).isEmpty()) {
            String[] headerParts = line.split(":", 2);
            if (headerParts.length == 2) {
                headers.put(headerParts[0].trim(), headerParts[1].trim());
            }
        }

        if (method.equals(HTTP_POST.getValue())) {
            StringBuilder bodyBuilder = new StringBuilder();
            int contentLength = Integer.parseInt(headers.getOrDefault("Content-Length", "0"));
            for (int i = 0; i < contentLength; i++) {
                bodyBuilder.append((char) br.read());
            }
            body = bodyBuilder.toString();
        }
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getHeader(String name) {
        return headers.get(name);
    }

    public String getBody() {
        return body;
    }

    public Map<String, String> parseQueryString() {
        Map<String, String> queryParams = new HashMap<>();

        // GET 쿼리 문자열 파싱
        if (path.contains("?")) {
            String urlQuery = path.split("\\?")[1];
            addParametersToMap(urlQuery, queryParams);
        }
        // POST 요청의 바디 파싱
        if (body != null && !body.isEmpty()) {
            addParametersToMap(body, queryParams);
        }

        return queryParams;
    }

    private void addParametersToMap(String queryString, Map<String, String> queryParams) {
        if (!queryString.isEmpty()) {
            for (String param : queryString.split("&")) {
                String[] keyValue = param.split("=", 2);
                queryParams.put(keyValue[0], keyValue.length > 1 ? keyValue[1] : "");
            }
        }
    }
    
    //request 출력
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        // 첫 줄: 요청 라인 (HTTP 메서드, 경로, HTTP 버전)
        sb.append(method).append(" ").append(path).append(" HTTP/1.1\r\n");

        // 헤더 추가
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\r\n");
        }

        // 바디가 존재할 경우 추가
        if (body != null && !body.isEmpty()) {
            sb.append("\r\n").append(body);
        }

        return sb.toString();
    }
}
