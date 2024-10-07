package http;

import Constant.StatusCode;

public class HttpResStartLine {
    private final String version;
    private final String statusCode;
    private final String statusMessage;


    public HttpResStartLine(StatusCode statusCode) {
        this.version = "HTTP/1.1";                      // 고정 가정
        this.statusCode = statusCode.getCode();
        this.statusMessage = statusCode.getMessage();
    }


    // Start Line을 출력하는 메서드
    public String toStartLine() {
        return version + " " + statusCode + " " + statusMessage;
    }
}
