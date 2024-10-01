package constant;

public enum StatusCode {

    OK("200 OK"),
    Found("302 Found");


    final String value;

    private StatusCode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}