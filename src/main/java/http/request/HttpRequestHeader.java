package http.request;

import java.io.BufferedReader;
import java.io.IOException;

import static constant.HttpHeader.CONTENT_LENGTH;
import static constant.HttpHeader.COOKIE;

public class HttpRequestHeader {

    private static int requestContentLength = 0;
    private static boolean isLogin = false;

    private HttpRequestHeader(int requestContentLength, boolean isLogin) {
        this.requestContentLength = requestContentLength;
        this.isLogin = isLogin;
    }

    public static HttpRequestHeader from(BufferedReader br) throws IOException {
        //header 추출
        while (true) {
            final String line = br.readLine();
            if (line.equals("")) {
                break;
            }
            // header info
            if (line.startsWith(CONTENT_LENGTH.getHeader())) {
                requestContentLength = Integer.parseInt(line.split(": ")[1]);
            }

            if (line.startsWith(COOKIE.getHeader()) && line.contains("logined=true")) {
                isLogin = true;
            }
        }

        return new HttpRequestHeader(requestContentLength, isLogin);
    }

    public boolean isEmptyContent() {
        return requestContentLength == 0;
    }

    public int getContentLength() {
        return requestContentLength;
    }
}
