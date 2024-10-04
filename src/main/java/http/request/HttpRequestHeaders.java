package http.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static http.HttpHeader.*;

public class HttpRequestHeaders {
    private final Map<String, String> headers = new HashMap<>();

    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    public String getHeader(String name) {
        return headers.get(name);
    }

    public int getContentLength() {
        String contentLength = headers.get(CONTENT_LENGTH.getValue());
        return contentLength == null ? 0 : Integer.parseInt(contentLength);
    }

    public static HttpRequestHeaders from(BufferedReader br) throws IOException {
        HttpRequestHeaders httpRequestHeaders = new HttpRequestHeaders();
        String line;
        while (!(line = br.readLine()).equals("")) {
            String[] headerTokens = line.split(": ");
            if (headerTokens.length == 2) {
                httpRequestHeaders.addHeader(headerTokens[0], headerTokens[1]);
            }
        }
        return httpRequestHeaders;
    }
}
