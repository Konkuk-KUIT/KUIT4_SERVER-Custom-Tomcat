package http.response;

public class HttpResponseStartLine {
    private static final String VERSION = "HTTP/1.1";
    private final int statusCode;
    private final String statusMessage;

    public HttpResponseStartLine(int statusCode, String statusMessage) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }

    public static HttpResponseStartLine of(int statusCode, String statusMessage) {
        return new HttpResponseStartLine(statusCode, statusMessage);
    }

    public String getStartLine() {
        return VERSION + " " + statusCode + " " + statusMessage;
    }
}
