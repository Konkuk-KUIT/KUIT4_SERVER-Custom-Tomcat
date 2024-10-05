package http.response;

public class HttpResponseStartLine {


    private String statusCode;
    private String message;

    private static final String VERSION = "HTTP/1.1";

    public HttpResponseStartLine(String statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public String getResponseStartLine(){
        return VERSION + " " + statusCode + " " + message + "\r\n";
    }
}
