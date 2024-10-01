package constant;

public enum HttpStatus {
    REDIRECT("302 REDIRECT"),
    OK("200 OK");

    private final String status;

    HttpStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
