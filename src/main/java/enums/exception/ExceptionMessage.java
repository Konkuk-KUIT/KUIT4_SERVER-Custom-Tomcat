package enums.exception;

public enum ExceptionMessage {
    INVALID_START_LINE("요청의 1번째 줄이 비어있습니다."),
    INVALID_REQUEST_URL("요청의 URL이 유효하지 않습니다.");

    private final String message;

    ExceptionMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
