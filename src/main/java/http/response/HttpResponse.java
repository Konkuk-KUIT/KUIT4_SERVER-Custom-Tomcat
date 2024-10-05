package http.response;

import http.HttpHeader;
import http.Url;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import static http.HttpHeader.*;
import static http.HttpStatusCode.*;
import static http.Url.*;

public class HttpResponse {
    private final DataOutputStream dos;
    private HttpResponseStartLine startLine;
    private final HttpResponseHeaders headers;
    private HttpResponseBody body;

    public HttpResponse(OutputStream dos) {
        this.dos = new DataOutputStream(dos);
        this.headers = new HttpResponseHeaders();
    }

    public void setStartLine(int statusCode, String statusMessage) {
        this.startLine = new HttpResponseStartLine(statusCode, statusMessage);
    }

    public void addHeader(String name, String value) {
        this.headers.addHeader(name, value);
    }

    public void setBody(String filePath) throws IOException {
        byte[] bodyContent = Files.readAllBytes(Paths.get(WEBAPP.getUrl() + filePath));
        this.body = new HttpResponseBody(bodyContent);
        addHeader(CONTENT_LENGTH.getValue(), String.valueOf(bodyContent.length));
    }

    public void forward(String filePath) throws IOException {
        setStartLine(OK.getCode(), OK.getMessage());
        setBody(filePath);

        String contentType = "text/html";
        if (filePath.endsWith(CSS_EXTENSION.getUrl())) {
            contentType = "text/css";
        }
        addHeader(CONTENT_TYPE.getValue(), contentType + ";charset=utf-8");

        sendResponse();  // 자동으로 응답을 전송
    }

    // 리다이렉트 응답 처리, 쿠키 포함 여부에 따라 처리
    public void redirect(String location, String cookie) throws IOException {
        setStartLine(FOUND.getCode(), FOUND.getMessage());
        addHeader(LOCATION.getValue(), location);

        if (cookie != null && !cookie.isEmpty()) {
            addHeader(SET_COOKIE.getValue(), cookie + "; Path=/");
        }

        sendResponse();  // 자동으로 응답을 전송
    }

    public void sendResponse() throws IOException {
        dos.writeBytes(startLine.getStartLine() + "\r\n");
        headers.writeHeaders(dos);
        dos.writeBytes("\r\n");
        if (body != null) {
            body.writeBody(dos);
        }
        dos.flush();
    }
}
