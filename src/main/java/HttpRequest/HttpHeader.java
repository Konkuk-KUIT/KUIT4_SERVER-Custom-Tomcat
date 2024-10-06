package HttpRequest;

import webserver.RequestHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import static Enum.HttpHeaderField.CONTENT_LENGTH;
import static Enum.HttpHeaderField.COOKIE;

public class HttpHeader {
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    private int contentLength = 0;
    private boolean logined = false;

    HttpHeader(BufferedReader br) {
        try {
            while (true) {
                final String line = br.readLine();
                if (line.isEmpty()) {
                    break;
                }
                // header info
                if (line.startsWith(CONTENT_LENGTH.getHttpHeader())) {
                    contentLength = Integer.parseInt(line.split(": ")[1]);
                }
                if (line.startsWith(COOKIE.getHttpHeader())) {
                    String[] cookieStrings = line.split(" ");
                    // 배열을 스트림으로 변환하여 조건에 맞는 값이 있으면 true 반환
                    logined = Arrays.asList(cookieStrings).contains("logined=true;");
                }
            }
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    public int getContentLength() {
        return contentLength;
    }

    public boolean isLogined() {
        return logined;
    }

}
