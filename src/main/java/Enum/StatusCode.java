package Enum;

public enum StatusCode {

    OK("HTTP/1.1 200 OK "),
    FOUND("HTTP/1.1 302 Found ")
    ;

    private final String statusCode;

    StatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusCode() {
        return statusCode;
    }
}
