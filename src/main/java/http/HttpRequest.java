package http;

import enums.exception.ExceptionMessage;
import http.util.HttpRequestUtils;
import http.util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import static enums.http.header.EntityHeader.*;

public class HttpRequest {
    private RequestStartLine startLine;
    private Map<String, String> headerMap;
    private Map<String, String> bodyMap;

    private HttpRequest(RequestStartLine startLine,
                       Map<String, String> headerMap,
                       Map<String, String> bodyMap) {
        this.startLine = startLine;
        this.headerMap = headerMap;
        this.bodyMap = bodyMap;
    }

    public static HttpRequest from(BufferedReader br) throws IOException {
        String rawStartLine = extractStartLine(br);
        RequestStartLine startLine = makeRequestStartLine(rawStartLine);
        Map<String, String> headerMap = extractHeaderMap(br);
        Map<String, String> bodyMap = extractBodyMap(br, headerMap);
        return new HttpRequest(startLine, headerMap, bodyMap);
    }

    private static String extractStartLine(BufferedReader br) throws IOException {
        String startLine = br.readLine();
        if (startLine == null || startLine.isEmpty()) {
            throw new IOException(ExceptionMessage.INVALID_START_LINE.getMessage());
        }
        return startLine;
    }

    private static RequestStartLine makeRequestStartLine(String rawStartLine){
        String httpMethod = rawStartLine.split(" ")[0];
        String rawUrl = rawStartLine.split(" ")[1];
        String url = extractUrl(rawUrl);
        Map<String, String> queryMap = extractQueryParameters(rawUrl);
        return RequestStartLine.from(httpMethod, url, queryMap);
    }

    private static String extractUrl(String rawUrl) {
        int questionMarkIndex = rawUrl.indexOf('?');
        String url = questionMarkIndex != -1 ? rawUrl.substring(0, questionMarkIndex) : rawUrl;

        if (url.endsWith("/")) url = url.substring(0, url.length() - 1);
        if (!url.startsWith("/")) url = "/" + url;

        return url;
    }

    private static Map<String, String> extractQueryParameters(String rawUrl) {
        int questionMarkIndex = rawUrl.indexOf('?');
        if (questionMarkIndex == -1) {
            return new HashMap<>();
        }
        String queryString = rawUrl.substring(questionMarkIndex + 1);
        return HttpRequestUtils.parseQueryParameter(queryString);
    }

    private static Map<String, String> extractHeaderMap(BufferedReader br) throws IOException {
        Map<String, String> headerMap = new HashMap<>();
        String line;
        while ((line = br.readLine()) != null) {
            if (line.isEmpty()) break;
            String[] headerParts = line.split(": ");
            if (headerParts.length == 2) {
                headerMap.put(headerParts[0], headerParts[1]);
            }
        }
        return headerMap;
    }

    private static Map<String, String> extractBodyMap(BufferedReader br, Map<String, String> headerMap) throws IOException {
        int contentLength = 0;
        String rawContentLength = headerMap.get(CONTENTLENGTH.getValue());
        if (rawContentLength != null) contentLength = Integer.parseInt(rawContentLength);

        String decodedMessageBody = "";
        if (contentLength > 0) {
            String rawMessageBody = IOUtils.readData(br, contentLength);
            decodedMessageBody = URLDecoder.decode(rawMessageBody, "UTF-8");
        }
        return buildBodyMapFromMessageBody(decodedMessageBody);
    }

    private static Map<String, String> buildBodyMapFromMessageBody(String rawMessageBody) {
        if (rawMessageBody.isEmpty()) return new HashMap<>();
        return HttpRequestUtils.parseQueryParameter(rawMessageBody);
    }

    public String getHttpMethod() {
        return startLine.getHttpMethod();
    }

    public String getUrl() {
        return startLine.getUrl();
    }

    public Map<String, String> getQueryMap() {
        return startLine.getQueryMap();
    }

    public Map<String, String> getHeaderMap() {
        return headerMap;
    }

    public Map<String, String> getBodyMap() {
        return bodyMap;
    }
}
