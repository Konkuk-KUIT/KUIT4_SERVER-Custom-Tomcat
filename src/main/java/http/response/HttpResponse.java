package http.response;

import http.constant.HttpHeaderType;
import http.constant.HttpStatus;
import http.request.HttpHeader;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

import static http.constant.HttpHeaderType.*;
import static http.constant.HttpStatus.*;
import static http.constant.HttpURL.*;

public class HttpResponse {
    private final OutputStream outputStream;
    private byte[] body;
    private final HttpHeader header;
    private final HttpResponseStartLine startLine;

    public HttpResponse(OutputStream outputStream) {
        this.outputStream = outputStream;
        this.body = new byte[0];
        header = new HttpHeader(new HashMap<>());
        header.putHeader(HttpHeaderType.CONTENT_TYPE,"text/css;charset=utf-8");
        startLine = new HttpResponseStartLine();
    }

    public void makeResponseMessage() throws IOException{
        outputStream.write((startLine.getVersion() + " " + startLine.getStatusCode() + " " + startLine.getHttpStatus() + "\r\n").getBytes());
        outputStream.write(header.toString().getBytes());
        outputStream.write(body);
        outputStream.flush();
    }

    public void addBodyContents(String path) throws IOException {
        this.body = Files.readAllBytes(Paths.get(ROOT.getUrl() + path));
        header.putHeader(CONTENT_LENGTH,String.valueOf(body.length));
    }

    public void forward(String targetPath) throws IOException {
        addBodyContents(targetPath);
        if(targetPath.endsWith(".html")){
            makeResponseMessage();
        }
        header.putHeader(CONTENT_TYPE,"text/css");
        makeResponseMessage();

    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public byte[] getBody() {
        return body;
    }

    public HttpHeader getHeader() {
        return header;
    }

    public HttpResponseStartLine getStartLine() {
        return startLine;
    }

    public void response302(String path) throws IOException {
        startLine.setHttpStatus(REDIRECT);
        startLine.setStatusCode("302");
        addBodyContents(path);
        makeResponseMessage();
    }
}
