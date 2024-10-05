package http.response;

import constant.HttpStatus;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static constant.HttpStatus.OK;

public class HttpResponseStartLine {
    private final OutputStream dos;
    private final String HTTP_VERSION = "HTTP/1.1 ";


    public HttpResponseStartLine(OutputStream dos) {
        this.dos = dos;
    }

    public void setStatus(HttpStatus status) throws IOException {
        dos.write((HTTP_VERSION+ status.getStatus()+" \r\n").getBytes());
    }
}
