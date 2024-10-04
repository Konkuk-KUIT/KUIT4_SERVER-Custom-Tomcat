package http;

import http.constant.HttpHeader;
import http.constant.HttpStatusCode;
import webserver.RequestHandler;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;


public class HttpResponse {
    private DataOutputStream dos;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    public HttpResponse(OutputStream out) {
        this.dos = new DataOutputStream(out);
    }

    public void forward(String body, String contentType) throws IOException {
        try {
            byte[] bodyBytes = body.getBytes();
            response200Header(bodyBytes.length, contentType);
            responseBody(bodyBytes);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Broken pipe in forward: " + e.getMessage(), e);
            throw e;
        }
    }

    public void redirect(String path) throws IOException {
        response302Header(path);
    }

    private void response200Header(int lengthOfBodyContent, String contentType) throws IOException {
        dos.writeBytes(HttpStatusCode.OK.getStatus() + " \r\n");
        dos.writeBytes(HttpHeader.CONTENT_TYPE.getHeader() + ": " + contentType + "\r\n");
        dos.writeBytes(HttpHeader.CONTENT_LENGTH.getHeader() + ": " + lengthOfBodyContent + "\r\n");
        dos.writeBytes("\r\n");
    }

    private void response302Header(String location) throws IOException {
        dos.writeBytes(HttpStatusCode.FOUND.getStatus() + " \r\n");
        dos.writeBytes(HttpHeader.LOCATION.getHeader() + ": " + location + "\r\n");
        dos.writeBytes("\r\n");
    }

    private void responseBody(byte[] body) throws IOException {
        dos.write(body, 0, body.length);
        dos.flush();
    }
}
