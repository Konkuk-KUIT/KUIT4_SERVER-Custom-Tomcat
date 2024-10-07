package http;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

import static Constant.HttpHeaderType.*;

public class HttpRequest {
    private final HttpReqStartLine httpReqStartLine;
    private final HttpReqHeader httpReqHeader;
    private final HttpReqBody httpReqBody;

    // 생성자
    private HttpRequest(HttpReqStartLine httpReqStartLine, HttpReqHeader httpReqHeader, HttpReqBody httpReqBody) {
        this.httpReqStartLine = httpReqStartLine;
        this.httpReqHeader = httpReqHeader;
        this.httpReqBody = httpReqBody;
    }

    // 정적 팩토리 메서드
    public static HttpRequest from(BufferedReader br) throws IOException {

        HttpReqStartLine httpReqStartLine = HttpReqStartLine.from(br.readLine());
        HttpReqHeader httpReqHeader = HttpReqHeader.from(br);

        HttpReqBody httpReqBody;
        if (httpReqHeader.getHeader(CONTENT_LENGTH.getValue()) != null) {                   //body 내용 있는지 확인
            int contentLength = Integer.parseInt(httpReqHeader.getHeader(CONTENT_LENGTH.getValue()));
            httpReqBody = HttpReqBody.from(br, contentLength);                    //body있을 경우 HttpBody 생성
        } else {
            httpReqBody = HttpReqBody.from(br, 0);                    //body 없을 경우 빈 문자열로 처리
        }

        return new HttpRequest(httpReqStartLine, httpReqHeader, httpReqBody);
    }

    // Getters
    public String getMethod() {
        return httpReqStartLine.getMethod();
    }

    public String getPath() {
        return httpReqStartLine.getPath();
    }

    public String getVersion() {
        return httpReqStartLine.getVersion();
    }

    public String getHeader(String key) {
        return httpReqHeader.getHeader(key);
    }

    public Map<String, String> getHeaders(){
        return httpReqHeader.getHeaders();
    }

    public String getBody(String key) {
        return httpReqBody.getBody(key);
    }
}
