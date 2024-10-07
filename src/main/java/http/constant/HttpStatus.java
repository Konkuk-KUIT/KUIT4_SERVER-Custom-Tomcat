package http.constant;

public enum HttpStatus {
    OK("OK"),
    REDIRECT("Redirect");


    private String status;

    HttpStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
