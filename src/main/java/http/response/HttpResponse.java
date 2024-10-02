package http.response;

import constant.Format;
import constant.URL;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import static constant.Format.CSS;
import static constant.Format.HTML;
import static constant.HttpHeader.*;
import static constant.HttpHeader.CONTENT_LENGTH;
import static constant.HttpStatus.OK;
import static constant.HttpStatus.REDIRECT;
import static constant.URL.ROOT;

public class HttpResponse {

    private final HttpResponseStartLine httpResponseStartLine;
    private final HttpResponseHeader httpResponseHeader;
    private final DataOutputStream dos;
    private byte[] body = "".getBytes();
    private final Logger log;

    public HttpResponse(DataOutputStream dos, Logger log) {
        this.log = log;
        this.dos = dos;
        httpResponseStartLine = new HttpResponseStartLine(dos);
        httpResponseHeader = new HttpResponseHeader(dos);
    }

    public void forward(String path) throws IOException {
        body = Files.readAllBytes(Paths.get(URL.ROOT.getUrl() + path));
        response200Header(body.length, HTML);
        responseBody();
    }

    public void redirect(String path, boolean isLogin) throws IOException {
        response302Header(path, isLogin);
    }

    public void setCss(String css) throws IOException {
        body = Files.readAllBytes(Paths.get(ROOT.getUrl() + css));
        response200Header(body.length, CSS);
        responseBody();
    }

    //요구사항 4(302 status code 적용)
    public void response302Header(String url, boolean isLogin) {
        try {
            httpResponseStartLine.setStatus(REDIRECT);
            httpResponseHeader.setLocation(url);
            httpResponseHeader.setCookie(isLogin);
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            printLog(e);
        }
    }

    public void response200Header(int lengthOfBodyContent, Format format) {
        try {
            httpResponseStartLine.setStatus(OK);
            httpResponseHeader.setContentType(format);
            httpResponseHeader.setContentLength(lengthOfBodyContent);
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            printLog(e);
        }
    }

    public void responseBody() {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            printLog(e);
        }
    }

    private void printLog(IOException e) {
        log.log(Level.SEVERE, e.getMessage());
    }

}
