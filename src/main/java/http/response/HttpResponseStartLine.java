package http.response;

import http.constant.HttpStatus;

import static http.constant.HttpStatus.*;

public class HttpResponseStartLine {
    private final String version = "HTTP/1.1";
    private String statusCode = "200";
    private HttpStatus httpStatus = OK;

    public HttpResponseStartLine() {
    }

    public String getVersion() {
        return version;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

}

