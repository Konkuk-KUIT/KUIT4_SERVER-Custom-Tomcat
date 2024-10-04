package enums.http;

public enum HttpStatus {
    OK("HTTP/1.1 200 OK \r\n"),
    REDIRECT("HTTP/1.1 302 Found \r\n");

    private final String value;

    HttpStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
