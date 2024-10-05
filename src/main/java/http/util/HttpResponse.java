package http.util;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static enumClass.HttpHeader.*;
import static enumClass.StatusCode.*;

public class HttpResponse {
    private DataOutputStream dos;

    public HttpResponse(DataOutputStream dos) {
        this.dos = dos;
    }

    public void forward(String path) throws IOException {
        byte[] body = Files.readAllBytes(Paths.get("webapp" + path));
        dos.writeBytes("HTTP/1.1 " + OK.getValue() + "\r\n");
        dos.writeBytes(CONTENT_TYPE.getValue() + ": text/html;charset=utf-8\r\n");
        dos.writeBytes(CONTENT_LENGTH.getValue() + ": " + body.length + "\r\n");
        dos.writeBytes("\r\n");
        dos.write(body, 0, body.length);
    }

    public void redirect(String path) throws IOException {
        dos.writeBytes("HTTP/1.1 " + FOUND.getValue() + "\r\n");
        dos.writeBytes(LOCATION.getValue() + ": " + path + "\r\n");
        dos.writeBytes("\r\n");
    }
}
