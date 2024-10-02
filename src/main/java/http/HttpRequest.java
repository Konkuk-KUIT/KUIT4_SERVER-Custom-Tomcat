package http;

import enums.exception.ExceptionMessage;
import http.util.HttpRequestUtils;
import http.util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static enums.http.header.EntityHeader.*;

public class HttpRequest {
    private String startLine;
    private Map<String, String> header;
    private Map<String, String> body;

    public HttpRequest(String startLine,
                       Map<String, String> header,
                       Map<String, String> body)
    {
        this.startLine = startLine;
        this.header = header;
        this.body = body;
    }

    public static HttpRequest from(BufferedReader br) throws IOException {
        // startLine
        String startLine = br.readLine();
        if (startLine == null || startLine.isEmpty()) {
            throw new IOException(ExceptionMessage.INVALID_START_LINE.getMessage());
        }

        // header
        Map<String, String> headerMap = new HashMap<>();
        String line;
        while ((line = br.readLine()) != null) {
            if (line.isEmpty()) break;
            String[] headerParts = line.split(": ");
            if (headerParts.length == 2) {
                headerMap.put(headerParts[0], headerParts[1]);
            }
        }

        int contentLength = 0;
        String rawContentLength = headerMap.get(CONTENTLENGTH.getValue());
        if(rawContentLength != null) contentLength= Integer.parseInt(rawContentLength);

        // message body 추출, 나중에 POST 할 때 Map 형식으로 변환
        String rawMessageBody = "";
        if (contentLength > 0) {
            rawMessageBody = IOUtils.readData(br, contentLength);
        }
        Map<String, String> bodyMap = extractBodyParameters(rawMessageBody);

        return new HttpRequest(startLine, headerMap, bodyMap);
    }

    private static Map<String, String> extractBodyParameters(String rawMessageBody) {
        if (rawMessageBody.isEmpty()) return new HashMap<>();
        return HttpRequestUtils.parseQueryParameter(rawMessageBody);
    }

    public String getHttpMethod() {
        return startLine.split(" ")[0];
    }

    public String getUrl() {
        String rawUrl = startLine.split(" ")[1];
        return extractUrl(rawUrl);
    }

    public Map<String, String> getQueryMap() {
        String rawUrl = startLine.split(" ")[1];
        return extractQueryParameters(rawUrl);
    }

    private String extractUrl(String rawUrl) {
        int questionMarkIndex = rawUrl.indexOf('?');
        String url = questionMarkIndex != -1 ? rawUrl.substring(0, questionMarkIndex) : rawUrl;

        if (url.endsWith("/")) url = url.substring(0, url.length() - 1);
        if (!url.startsWith("/")) url = "/" + url;

        return url;
    }

    private Map<String, String> extractQueryParameters(String rawUrl) {
        int questionMarkIndex = rawUrl.indexOf('?');
        if (questionMarkIndex == -1) {
            return new HashMap<>();
        }
        String queryString = rawUrl.substring(questionMarkIndex + 1);
        return HttpRequestUtils.parseQueryParameter(queryString);
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public Map<String, String> getBody() {
        return body;
    }
}
