package http.request;

import java.io.BufferedReader;
import java.io.IOException;

import static http.HttpHeaders.CONTENT_LENGTH;
import static http.HttpHeaders.COOKIE;

public class HttpRequestHeader {

    private final int contentLength;
    private final String cookie;

    private HttpRequestHeader(int contentLength, String cookie) {
        this.contentLength = contentLength;
        this.cookie = cookie;
    }

    public static HttpRequestHeader createHttpHeader(BufferedReader br) throws IOException {

        int contentLength = 0;
        String cookie = "";

        while (true) {
            final String line = br.readLine();
            if (line.isEmpty()) {
                break;
            }
            if (line.startsWith(CONTENT_LENGTH.getHeader())) {
                contentLength = Integer.parseInt(line.split(": ")[1]);
            }
            if (line.startsWith(COOKIE.getHeader())) {
                cookie = line.split(": ")[1].trim(); // 쿠키 값 읽고 공백 제거
            }
        }
        return new HttpRequestHeader(contentLength,cookie);
    }

    public int getContentLength() {
        return contentLength;
    }

    public String getCookie() {
        return cookie;
    }
}
