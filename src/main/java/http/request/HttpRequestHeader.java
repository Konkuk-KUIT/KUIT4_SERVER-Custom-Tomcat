package http.request;

import java.io.BufferedReader;
import java.io.IOException;

import static constant.HttpHeader.CONTENT_LENGTH;
import static constant.HttpHeader.COOKIE;

public class HttpRequestHeader {

    private final int requestContentLength;
    private final boolean LoginStatus;

    private HttpRequestHeader(int requestContentLength, boolean LoginStatus) {
        this.requestContentLength = requestContentLength;
        this.LoginStatus = LoginStatus;
    }

    public static HttpRequestHeader from(BufferedReader br) throws IOException {
        int contentLength = 0;
        boolean isLogin = false;

        //header 추출
        while (true) {
            final String line = br.readLine();
            if (line.equals("")) {
                break;
            }
            // header info
            if (line.startsWith(CONTENT_LENGTH.getHeader())) {
                contentLength = Integer.parseInt(line.split(": ")[1]);
            }

            if (line.startsWith(COOKIE.getHeader()) && line.contains("logined=true")) {
                isLogin = true;
            }

        }

        return new HttpRequestHeader(contentLength, isLogin);
    }

    public boolean isEmptyContent() {
        return requestContentLength == 0;
    }

    public int getContentLength() {
        return requestContentLength;
    }

    public boolean getLoginStatus() {
        return LoginStatus;
    }
}
