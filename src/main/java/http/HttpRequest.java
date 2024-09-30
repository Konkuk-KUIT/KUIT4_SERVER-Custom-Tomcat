package http;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private String method;
    private String path;
    private String version;
    private Map<String, String> headers = new HashMap<>();
    private String body;

    // 정적 팩토리 메서드: BufferedReader로부터 HttpRequest 생성
    public static HttpRequest from(BufferedReader br) throws IOException {
        HttpRequest request = new HttpRequest();

        // 1. Start Line 분석 (Method, Path, Version)
        String startLine = br.readLine();
        if (startLine != null) {
            String[] startLineParts = startLine.split(" ");
            request.method = startLineParts[0];  // GET, POST 등
            request.path = startLineParts[1];    // /index.html
            request.version = startLineParts[2]; // HTTP/1.1
        }

        // 2. Header 분석
        String headerLine;
        while (!(headerLine = br.readLine()).isEmpty()) {
            String[] headerParts = headerLine.split(": ");
            if (headerParts.length == 2) {
                request.headers.put(headerParts[0], headerParts[1]);
            }
        }

        // 3. Body 읽기 (Content-Length가 있는 경우에만)
        if (request.headers.containsKey("Content-Length")) {
            int contentLength = Integer.parseInt(request.headers.get("Content-Length"));
            char[] bodyChars = new char[contentLength];
            br.read(bodyChars, 0, contentLength);
            request.body = new String(bodyChars);
        }

        return request;
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
}