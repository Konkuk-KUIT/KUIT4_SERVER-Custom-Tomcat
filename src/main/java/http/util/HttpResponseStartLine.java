package http.util;

public class HttpResponseStartLine {

    private String version;
    private String statusCode;
    private String message;

    public HttpResponseStartLine(String version, String statusCode, String message) {
        this.version = version;
        this.statusCode = statusCode;
        this.message = message;
    }

    public String getVersion() {
        return version;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }
}
