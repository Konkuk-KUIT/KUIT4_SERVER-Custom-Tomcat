package webserver;

import http.util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;

import static strings.FilePath.*;
import static strings.FilePath.STYLES_CSS;

public class HttpRequest {
    private String startLine;
    private String method;
    private String url;
    private String version;
    private String header;
    private String body;

    private HttpRequest(String startLine, String header, String body) {
        this.startLine = startLine;
        this.header = header;
        this.body = body;
    }

    public static HttpRequest from(BufferedReader br) throws IOException {
        String startLine = extractStartLine(br);
        String header = extractRequestHeader(br);
        String body = extractRequestBody(br);

        if (areNull(startLine, header, body) || areEmpty(startLine, header)) {
            throw new IllegalArgumentException("Arguments must not be null or empty");
        }
        return new HttpRequest(startLine, header, body);
    }

    public String getStartLine() {
        return startLine;
    }

    public String getHeader() {
        return header;
    }

    public String getBody() {
        return body;
    }

    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public String getVersion() {
        return version;
    }

    public boolean cookieLoginTrue() {
        if (header.startsWith("Cookie:")) {
            return header.contains("true");
        }
        return false;
    }


    private static String extractStartLine(BufferedReader br) throws IOException {
        try {
            String startLine = br.readLine();
            if (startLine != null) {
                return br.readLine();
            }
            return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String extractRequestHeader(BufferedReader br) throws IOException {
        String headers;

        while ((headers = br.readLine()) != "") {
            headers = br.readLine();
        }

        return headers;
    }

    private static String extractRequestBody(BufferedReader br) throws IOException {
        int requestContentLength = 0;

        while (true) {
            final String line;
            line = br.readLine();
            if (line.equals("")) { break; }

            // header info
            if (line.startsWith("Content-Length")) {
                requestContentLength = Integer.parseInt(line.split(": ")[1]);
            }
        }

        String body = IOUtils.readData(br, requestContentLength);

        return body;
    }

    private void extractStartLineElements() {
        if (startLine != null) {
            String[] tokens = startLine.split(" ");
            if (tokens.length > 1) {
                method = tokens[0];
                url = tokens[1];
                version = tokens[2];
            }
        }
    }

    private static boolean areNull(String startLine, String header, String body) {
        return startLine == null || header == null || body == null;
    }

    private static boolean areEmpty(String startLine, String header) {
        return startLine.isEmpty() || header.isEmpty();
    }

    public String[] getValueOfURLQuery(String urlQuery) {
        String[] buf = urlQuery.split("\\?");
        String queryString = buf[1];

        return getValueOfQuery(queryString);
    }

    public String[] getValueOfQuery(String query) {
        String[] keyAndValue = query.split("&");

        String[] value = new String[keyAndValue.length];

        for (int i = 0; i < keyAndValue.length; i++) {
            value[i] = keyAndValue[i].split("=")[1];
            System.out.println(value[i]);
        }

        return value;
    }

}
