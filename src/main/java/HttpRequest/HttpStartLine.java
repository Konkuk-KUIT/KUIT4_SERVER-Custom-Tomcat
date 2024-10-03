package HttpRequest;

import webserver.RequestHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpStartLine {
    private String method;
    private String url;
    private String version;

    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    HttpStartLine(BufferedReader br) {
        try {
            initHttpStartLine(br);
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void initHttpStartLine(BufferedReader br) throws IOException {
        try {
            String httpStartLine = br.readLine();
            String[] elements = httpStartLine.split(" ");
            method = elements[0];
            url = elements[1];
            version = elements[2];
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }

    }

    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public String getVersion() {
        return version;
    }
}
