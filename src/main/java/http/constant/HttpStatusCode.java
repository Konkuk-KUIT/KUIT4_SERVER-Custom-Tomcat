package http.constant;

public enum HttpStatusCode {
    OK(200, "200 OK"),
    FOUND(302, "302 Found");

    private final int code;
    private final String status;

    HttpStatusCode(int code, String status) {
        this.code = code;
        this.status = status;
    }

    public int getCode() {
        return code;
    }

    public String getStatus() {
        return status;
    }
}
