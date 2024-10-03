package http.util;

import http.HttpRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static http.util.IOUtils.readData;

public class HttpRequestUtils {

    public static HttpRequest parse(BufferedReader br) throws IOException {
        String method = null;
        String path = null;
        String version = null;
        Map<String, String> headers = new HashMap<>();
        String body = null;
        String queryString = null;

        // 1. Start Line 분석 (Method, Path, Version)
        String startLine = br.readLine();
        if (startLine != null) {
            String[] startLineParts = startLine.split(" ");
            method = startLineParts[0];  // GET, POST 등
            String fullPath = startLineParts[1]; // /index.html?param=value
            version = startLineParts[2]; // HTTP/1.1
            int queryIndex = fullPath.indexOf('?');
            if (queryIndex != -1) {
                path = fullPath.substring(0, queryIndex);
                queryString = fullPath.substring(queryIndex + 1);
            } else {
                path = fullPath;
                queryString = null;
            }
        }

        // 2. Header 분석
        String headerLine;
        while (!(headerLine = br.readLine()).isEmpty()) {
            String[] headerParts = headerLine.split(": ");
            if (headerParts.length == 2) {
                headers.put(headerParts[0], headerParts[1]);
            }
        }

        // body 읽기
        if (headers.containsKey("Content-Length")) {
            int contentLength = Integer.parseInt(headers.get("Content-Length"));
            body = readData(br, contentLength);
        }

        // HttpRequest 객체 생성 및 반환
        return new HttpRequest(method, path, version, headers, body, queryString);
    }

    public static Map<String, String> parseQueryParameter(String queryString) {
        try {
            String[] queryStrings = queryString.split("&");

            return Arrays.stream(queryStrings)
                    .map(q -> q.split("="))
                    .collect(Collectors.toMap(queries -> queries[0], queries -> queries[1]));
        } catch (Exception e) {
            return new HashMap<>();
        }
    }
}
