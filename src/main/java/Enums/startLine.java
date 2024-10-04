package Enums;

public enum startLine {

    RESPONSE_FOUND("HTTP/1.1 302 Found "),RESPONSE_OK("HTTP/1.1 200 OK ");

    private String text;

    private startLine(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
