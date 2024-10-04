package http.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private HttpRequestStartLine httpRequestStartLine;
    private Map<String, String> httpHeader;
    private String body;
//    private Map<String, String> parameters; // URL 파라미터를 저장할 맵 추가

    public HttpRequest(HttpRequestStartLine httpRequestStartLine, Map<String, String> httpHeader, String body) {
        this.httpRequestStartLine = httpRequestStartLine;
        this.httpHeader = httpHeader;
        this.body = body;
    }

    public static HttpRequest from(BufferedReader br) throws IOException {
        // httpStartLine
        String startLine = br.readLine();
        String[] tokens = startLine.split(" ");
        HttpRequestStartLine httpRequestStartLine = new HttpRequestStartLine(tokens[0], tokens[1], tokens[2]);

        // httpHeader
        Map<String, String> map = new HashMap<>();
        while (true) {
            final String line = br.readLine();
            if (line.equals("")) {
                break;
            }
            map.put(line.split(": ")[0], line.split(": ")[1]);
        }

        // body
        String requestBody = "";
        if (map.containsKey("Content-Length")) {
            int requestContentLength = Integer.parseInt(map.get("Content-Length"));
            if (requestContentLength > 0) {
                requestBody = IOUtils.readData(br, requestContentLength);
                System.out.println(requestBody);
            }
        }

        return new HttpRequest(httpRequestStartLine, map, requestBody);
    }

//    public String getParameter(String p) {
//
//    }

    public String getMethod() {
        return httpRequestStartLine.getMethod();
    }

    public String getUrl() {
        return httpRequestStartLine.getPath();
    }

    public String getVersion() {
        return httpRequestStartLine.getVersion();
    }

    public String getBody() {
        return body;
    }

}
