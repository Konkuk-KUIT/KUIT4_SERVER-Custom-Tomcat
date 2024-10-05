package http;

import http.constant.HttpHeader;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private String method;
    private String path;
    private Map<String, String> headers = new HashMap<>();
    private String body;

    private HttpRequest() {
    }

    public static HttpRequest from(BufferedReader reader) throws IOException {
        HttpRequest httpRequest = new HttpRequest();
        String line = reader.readLine();
        if (line == null || line.isEmpty()) {
            throw new IOException("Invalid HTTP request");
        }

        String[] requestLineTokens = line.split(" ");
        httpRequest.method = requestLineTokens[0];
        httpRequest.path = requestLineTokens[1];

        while (!(line = reader.readLine()).isEmpty()) {
            String[] headerTokens = line.split(": ");
            httpRequest.headers.put(headerTokens[0], headerTokens[1]);
        }

        if (httpRequest.headers.containsKey(HttpHeader.CONTENT_TYPE.getHeader())) {
            int contentLength = Integer.parseInt(httpRequest.headers.get(HttpHeader.CONTENT_LENGTH.getHeader()));
            char[] bodyChars = new char[contentLength];
            reader.read(bodyChars, 0, contentLength);
            httpRequest.body = new String(bodyChars);
        }

        return httpRequest;
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

    public Map<String, String> getHeaders() {
        return headers;
    }
}
