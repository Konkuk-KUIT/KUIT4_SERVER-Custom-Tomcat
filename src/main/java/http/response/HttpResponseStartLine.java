package http.response;

import http.StatusCode;

public class HttpResponseStartLine {

    private final String VERSION = "HTTP/1.1 ";
    private final StatusCode statusCode;

    private HttpResponseStartLine(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public static HttpResponseStartLine createHttpResponseStartLine(StatusCode statusCode){
        return new HttpResponseStartLine(statusCode);
    }

    public String getHttpResponseStartLine() {
        return VERSION+statusCode.getCode()+" "+statusCode.getMessage()+" \r\n";
    }
}
