package HttpRequest;

import http.util.IOUtils;
import webserver.RequestHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpBody {

    private String body;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    HttpBody(BufferedReader br, int requestContentLength) {
        try {
            body = IOUtils.readData(br, requestContentLength);
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    public String getBody() {
        return body;
    }
}
