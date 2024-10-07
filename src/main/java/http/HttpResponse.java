package http;

import Constant.HttpHeaderType;
import Constant.StatusCode;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static Constant.HttpContentType.*;
import static Constant.HttpHeaderType.*;
import static Constant.StatusCode.*;
import static Constant.Url.*;

public class HttpResponse {
    private final DataOutputStream dos;
    private HttpResStartLine startLine;
    private final HttpResHeader headers;
    private final HttpResBody body;

    public HttpResponse(OutputStream os) {
        this.dos = new DataOutputStream(os);
        this.startLine = new HttpResStartLine(OK);       // 200 상태 코드로 초기화
        this.headers = new HttpResHeader();
        this.body = new HttpResBody();
    }

    public void addHeader(HttpHeaderType header, String value) {
        headers.addHeader(header, value);
    }

    // 응답 본문 전송
    public void forward(String path) throws IOException {
        body.loadBodyFromFile(path);

        addHeader(CONTENT_LENGTH, String.valueOf(body.getBodyLength()));

        if (path.endsWith(URL_HTML.getMessage())) {
            addHeader(CONTENT_TYPE, HTML.getCode());
        } else if (path.endsWith(URL_CSS.getMessage())) {
            addHeader(CONTENT_TYPE, CSS.getCode());
        }

        writeResponse();
    }

    // 리디렉션 처리
    public void redirect(String path) throws IOException {     //302
        setStatus(FOUND);
        addHeader(LOCATION, path);

        writeResponse();
    }

    // 상태 코드 설정
    public void setStatus(StatusCode statusCode) {              // 새로운 상태 코드로 Start Line 갱신
        this.startLine = new HttpResStartLine(statusCode);

    }

    // 응답 메시지 전송
    private void writeResponse() throws IOException {
        // StartLine 작성
        dos.writeBytes(startLine.toStartLine() + "\r\n");

        // Headers 작성
        dos.writeBytes(headers.toHeaders());

        // 본문 작성
        if (body.getBody() != null) {
            dos.write(body.getBody(), 0, body.getBodyLength());
        }

        dos.flush();
    }
}
