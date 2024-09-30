package webserver;

public enum StatusCode {
    REDIRECT("302 REDIRECT"),
    OK("200 OK");

    private final String status;

    StatusCode(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
