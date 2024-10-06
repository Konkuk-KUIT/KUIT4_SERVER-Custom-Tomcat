package http.response;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static http.StatusCode.*;
import static http.request.Url.WEBAPP_PATH;

public class HttpResponse {

    private static final Logger log = Logger.getLogger(HttpResponse.class.getName());
    private final DataOutputStream dos;
    private HttpResponseStartLine responseStartLine;
    private HttpResponseHeader httpResponseHeader;
    private HttpResponseBody httpResponseBody;


    public HttpResponse(DataOutputStream dos) {
        this.dos=dos;
    }

    public void forward(String Path) throws IOException {

        String filePath = WEBAPP_PATH.getPath() + Path;
        File file = new File(filePath);

        // 파일 존재 여부에 따른 HTTP 응답 시작 줄 설정
        if (file.exists()) {
            responseStartLine = HttpResponseStartLine.createHttpResponseStartLine(OK);
        } else {
            responseStartLine = HttpResponseStartLine.createHttpResponseStartLine(NOT_FOUND);
        }
        httpResponseHeader = HttpResponseHeader.createBasicHttpResponseHeader(file);
        httpResponseBody = HttpResponseBody.createHttpResponseBody(file);

        sendResponse();
    }

    public void redirect(String Path) throws IOException {

        responseStartLine = HttpResponseStartLine.createHttpResponseStartLine(FOUND);
        httpResponseHeader = HttpResponseHeader.createRedirectHttpResponseHeader(Path);
        httpResponseBody = null;

        sendResponse();
    }

    public void redirectWithCookie(String Path) throws IOException {

        responseStartLine = HttpResponseStartLine.createHttpResponseStartLine(FOUND);
        httpResponseHeader = HttpResponseHeader.createCookieHttpResponseHeader(Path);
        httpResponseBody = null; // 쿠키 리다이렉트 시 본문은 없음

        sendResponse();
    }

    private void sendResponse() throws IOException {
        try {
            dos.writeBytes(responseStartLine.getHttpResponseStartLine());
            dos.writeBytes(httpResponseHeader.getHttpResponseHeader());
            if (httpResponseBody != null) {
                dos.write(httpResponseBody.getBody());
            }
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

}
