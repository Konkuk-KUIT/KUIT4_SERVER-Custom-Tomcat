package http.response;

import http.request.HttpRequestHeader;
import http.request.HttpRequest;
import http.request.HttpRequestStartLine;
import http.request.HttpRequestBody;
import http.util.IOUtils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static http.StatusCode.*;

public class HttpResponse {

    private static final Logger log = Logger.getLogger(HttpResponse.class.getName());
    private static final String WEBAPP_PATH = "./webapp";
    private final DataOutputStream dos;
//    private final HttpResponseStartLine httpResponseStartLine;
//    private final HttpResponseHeader httpResponseHeader;
//    private final HttpResponseBody httpResponseBody;

    public HttpResponse(DataOutputStream dos) {
        this.dos=dos;
    }

//    public void setStatusLine(String version, int statusCode, String statusMessage) {
//        this.statusLine = version + " " + statusCode + " " + statusMessage + "\r\n";
//    }

  //  HttpResponseStartLine httpResponseStartLine =



    //outputStream을 인자로 받아 원하는 html 파일을 보여주는 forward(path) 함수와 redirect 시켜주는 redirect(path) 함수를 구현해보자.
    public void forward(String Path) throws IOException {

        String filePath = WEBAPP_PATH + Path;
        File file = new File(filePath);

        String httpResponseStartLine;
        // 파일 존재 여부에 따른 HTTP 응답 시작 줄 설정
        if (file.exists())
            httpResponseStartLine = HttpResponseStartLine.createHttpResponseStartLine(OK).getHttpResponseStartLine();
        else
            httpResponseStartLine = HttpResponseStartLine.createHttpResponseStartLine(NOT_FOUND).getHttpResponseStartLine();

        // HTTP 응답 헤더와 본문 생성
        String httpResponseHeader = HttpResponseHeader.createBasicHttpResponseHeader(file).getHttpResponseHeader();
        byte[] httpResponseBody = HttpResponseBody.createHttpResponseBody(file).getbody();

        // HTTP 응답 전송
        try {
            // 시작 줄 전송
            dos.writeBytes(httpResponseStartLine);
            // 헤더 전송
            dos.writeBytes(httpResponseHeader);
            // 본문 전송
            dos.write(httpResponseBody);
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    public void redirect(String Path){

        String httpResponseStartLine = HttpResponseStartLine.createHttpResponseStartLine(FOUND).getHttpResponseStartLine();
        String httpResponseHeader = HttpResponseHeader.createRedirectHttpResponseHeader(Path).getHttpResponseHeader();

        // HTTP 응답 전송
        try {
            // 시작 줄 전송
            dos.writeBytes(httpResponseStartLine);
            // 헤더 전송
            dos.writeBytes(httpResponseHeader);
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    public void redirectWithCookie(String Path){

        String httpResponseStartLine = HttpResponseStartLine.createHttpResponseStartLine(FOUND).getHttpResponseStartLine();
        String httpResponseHeader = HttpResponseHeader.createCookieHttpResponseHeader(Path).getHttpResponseHeader();

        // HTTP 응답 전송
        try {
            // 시작 줄 전송
            dos.writeBytes(httpResponseStartLine);
            // 헤더 전송
            dos.writeBytes(httpResponseHeader);
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }

    }





}
