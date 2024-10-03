package http.response;

import constant.HttpHeaderTitle;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static constant.HttpHeaderTitle.*;
import static constant.StatusCode.*;
import static constant.Url.CSS_EXTENSION;
import static constant.Url.WEBAPP;

public class HttpResponse {

    private HttpResponseStartLine httpResponseStartLine;
    private HttpResponseHeader httpResponseHeader;
    private ResponseBody responseBody;

    private DataOutputStream dos;

    public HttpResponse(OutputStream dos) {
        this.dos = new DataOutputStream(dos);
        httpResponseHeader = new HttpResponseHeader(new HashMap<>());
    }

    public void forward(String path) throws IOException {
        // startline 설정
        setStartLine(OK.getStatusCode(), OK.getMessage());

        // body 설정
        responseBody = new ResponseBody(Files.readAllBytes(Paths.get(WEBAPP.getUrl() + path)));

        // forward 하는데 필요한 header 삽입
        putHeader(CONTENT_LENGTH.getHeaderTitle(), String.valueOf(responseBody.getBodyLength()));

        String type = "text/html";
        if(path.contains(CSS_EXTENSION.getUrl())){
            type = "text/css";
        }

        // 파일 형식에 따른 헤더 삽입
        putHeader(CONTENT_TYPE.getHeaderTitle(), type+";charset=utf-8");

        // 헤더들을 outputStream에 적어주기
        writeResponseHeader();

        dos.write(responseBody.getBodyContent(), 0, responseBody.getBodyLength());
        dos.flush();
    }

    public void redirect(String path) throws IOException {
        // startLine 설정
        setStartLine(Found.getStatusCode(), Found.getMessage());

        // redirect에 필요한 헤더 삽입
        putHeader(LOCATION.getHeaderTitle(), path);

        writeResponseHeader();
    }

    // startline 설정
    private void setStartLine(String statusCode, String message) {
        httpResponseStartLine = new HttpResponseStartLine(statusCode, message);
    }

    // 헤더를 맵에 삽입
    public void putHeader(String key, String value) {
        httpResponseHeader.putHeader(key, value);
    }

    private void writeResponseHeader() throws IOException {
        // startline을 outputStream에 적어주기
        dos.writeBytes(httpResponseStartLine.getReponseStartLine());

        // 헤더들을 outputStream에 적어주기
        for(Map.Entry<String, String> entry : httpResponseHeader.getHeaderMap().entrySet()) {
            dos.writeBytes(entry.getKey() + ": " + entry.getValue() + "\r\n");
        }

        dos.writeBytes("\r\n");
    }





}
