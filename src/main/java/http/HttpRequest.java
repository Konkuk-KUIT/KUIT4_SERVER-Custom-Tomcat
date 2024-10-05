package http;

import constants.HttpHeader;
import constants.HttpMethod;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private final HttpMethod method;
    private final String path;
    private final String version;
    private final Map<HttpHeader, String> headers;
    private final String body;

    private HttpRequest(HttpMethod method, String path, String version, Map<HttpHeader, String> headers, String body) {
        this.method = method;
        this.path = path;
        this.version = version;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest from(BufferedReader br) throws IOException {

        // Start Line: method, path, version 추출
        String startLine = br.readLine();

        if (startLine == null) {
            throw new IOException("Received an empty request.");
        }

        String[] parts = startLine.split(" ");
        HttpMethod method = HttpMethod.fromString(parts[0]);
        String path = parts[1];
        String version = parts[2];

        // Header 추출
        Map<HttpHeader, String> headers = new HashMap<>();
        String line;
        while (!(line = br.readLine()).isEmpty()) {
            String[] headerParts = line.split(": ", 2);
            if (headerParts.length == 2) {
                try {
                    headers.put(HttpHeader.valueOf(headerParts[0].toUpperCase().replace("-", "_")), headerParts[1].trim());
                } catch (IllegalArgumentException e) {
                }
            }
        }

        // Body 추출 (POST 요청일 경우)
        StringBuilder bodyBuilder = new StringBuilder();
        if (method.equals(HttpMethod.POST)) {
            int contentLength = Integer.parseInt(headers.get(HttpHeader.CONTENT_LENGTH));
            char[] bodyChars = new char[contentLength];
            br.read(bodyChars);
            bodyBuilder.append(bodyChars);
        }

        return new HttpRequest(method, path, version, headers, bodyBuilder.toString());
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Map<HttpHeader, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }
}