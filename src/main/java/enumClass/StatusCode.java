package enumClass;

public enum StatusCode {

    OK("200 OK"),
    FOUND("302 Found"),
    NOTFOUND("404 Not Found");

    final String value;

    private StatusCode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
