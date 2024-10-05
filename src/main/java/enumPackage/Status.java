package enumPackage;

public enum Status {
    STATUS200("200"),
    STATUS302("302"),
    OK("OK"),
    FOUND("Found")
    ;

    private final String status;

    Status(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
