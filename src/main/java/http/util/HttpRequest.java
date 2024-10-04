package http.util;

import enumClass.HttpMethod;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static enumClass.HttpMethod.*;

public class HttpRequest {
    private String method;
    private String path;
    private String version;
    private Map<String, String> headers = new HashMap<>();
    private String body;

    private HttpRequest(String method, String path, String version, Map<String, String> headers, String body) {
        this.method = method;
        this.path = path;
        this.version = version;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest from(BufferedReader br) throws IOException {
        // Parse request start line
        String startLine = br.readLine();
        if (startLine == null || startLine.isEmpty()) {
            throw new IOException("Empty request");
        }

        String[] tokens = startLine.split(" ");
        String method = tokens[0];
        String path = tokens[1];
        String version = tokens[2];

        // Parse headers
        Map<String, String> headers = new HashMap<>();
        String headerLine;
        while (!(headerLine = br.readLine()).isEmpty()) {
            String[] headerTokens = headerLine.split(": ");
            headers.put(headerTokens[0], headerTokens[1]);
        }

        // Parse body if Content-Length exists
        String body = null;
        if (headers.containsKey("Content-Length")) {
            int contentLength = Integer.parseInt(headers.get("Content-Length"));
            char[] bodyChars = new char[contentLength];
            br.read(bodyChars, 0, contentLength);
            body = new String(bodyChars);
        }

        return new HttpRequest(method, path, version, headers, body);
    }

    public String getMethod() {
        return method;
    }

    public boolean isMethod(HttpMethod httpMethod) {
        return this.method.equals(httpMethod.getValue());
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
