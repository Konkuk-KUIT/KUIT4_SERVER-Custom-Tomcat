package http.response;

import java.io.DataOutputStream;

public class HttpResponseStartLine {


    private String statusCode;
    private String message;

    private static final String VERSION = "HTTP/1.1";

    public HttpResponseStartLine(String statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public String getVersion() {
        return VERSION;
    }
}
