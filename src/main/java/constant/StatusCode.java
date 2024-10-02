package constant;

public enum StatusCode {

    OK("200 OK"),
    Found("302 Found");


    final String code;

    private StatusCode(String code) {
        this.code = code;
    }

    public String getStatusCode() {
        return code;
    }
}