package http.response;

import constant.HttpStatus;

import java.io.DataOutputStream;
import java.io.IOException;

import static constant.HttpStatus.OK;

public class HttpResponseStartLine {
    private final DataOutputStream dos;
    private final String HTTP_VERSION = "HTTP/1.1 ";


    public HttpResponseStartLine(DataOutputStream dos) {
        this.dos = dos;
    }

    public void setStatus(HttpStatus status) throws IOException {
        dos.writeBytes(HTTP_VERSION+ status.getStatus()+" \r\n");
    }
}
