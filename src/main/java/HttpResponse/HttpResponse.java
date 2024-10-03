package HttpResponse;

import webserver.RequestHandler;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import static Enum.HttpHeaderField.*;
import static Enum.StatusCode.FOUND;
import static Enum.StatusCode.OK;
import static Enum.Url.STYLES_CSS;

public class HttpResponse {

    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    private DataOutputStream dos;

    public HttpResponse(DataOutputStream dos) {
        this.dos = dos;
    }

    public void forward(String path) {
        try {
            byte[] body = Files.readAllBytes(Paths.get(path));
            String contentType = path.endsWith(".css") ? "css" : "html";
            response200Header(dos, contentType, body.length);
            responseBody(dos, body);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void redirect(String path) {
        response302Header(dos, path, null);
    }

    public void redirect(String path, String cookieString) {
        response302Header(dos, path, cookieString);
    }

    private void response200Header(DataOutputStream dos, String contentType, int lengthOfBodyContent) {
        try {
            dos.writeBytes(OK.getStatusCode() + "\r\n");
            dos.writeBytes(CONTENT_TYPE.getHttpHeader() + "text/" + contentType + ";charset=utf-8\r\n");
            dos.writeBytes(CONTENT_LENGTH.getHttpHeader() + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String redirectUrl, String cookieString) {
        try {
            dos.writeBytes(FOUND.getStatusCode() + "\r\n");
            dos.writeBytes(LOCATION.getHttpHeader() + redirectUrl + "\r\n");
            if (cookieString != null) {
                dos.writeBytes(SET_COOKIE.getHttpHeader() + cookieString + "\r\n");
            }
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }


    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }
}
