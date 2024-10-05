package constant;

public enum StatusCode {

    OK("200", "OK"),
    Found("302", "Found");


    final String code;
    final String message;

    private StatusCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getStatusCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return code + " " + message;
    }
}