package webserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class HttpRequest2 {
    private HttpMethod method;
    private String path;
    private Map<String, String> headers;
    private String body;

    private HttpRequest2(HttpMethod method, String path, Map<String,String> headers, String body){
        this.method = method;
        this.path = path;
        this.headers = headers;
        this.body = body;
    }
    public static HttpRequest2 from(BufferedReader br) throws IOException {
        String requestLine = br.readLine();
        String[] requestParts = requestLine.split(" ");
        HttpMethod method = HttpMethod.valueOf(requestParts[0].toUpperCase());
        String path = requestParts[1];

        Map<String, String> headers = new HashMap<>();
        String line;
        while (!(line = br.readLine()).isEmpty()) {
            String[] headerParts = line.split(": ");
            headers.put(headerParts[0], headerParts[1]);
        }

        String body = "";
        if (headers.containsKey(HttpHeader.CONTENT_LENGTH.getHeader())) {
            int contentLength = Integer.parseInt(headers.get(HttpHeader.CONTENT_LENGTH.getHeader()));
            char[] bodyChars = new char[contentLength];
            br.read(bodyChars, 0, contentLength);
            body = new String(bodyChars);
        }

        return new HttpRequest2(method, path, headers, body);
    }
    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    public String getBody() {
        return body;
    }
}
