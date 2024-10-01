package enums.exception;

public enum ExceptionMessage {
    INVALID_STARTLINE("요청의 1번째 줄이 비어있습니다.");

    private final String message;

    ExceptionMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
