package webserver;

public enum StatusCode {
    OK(200, "OK"),
    FOUND(302, "Found"),
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    NOT_FOUND(404, "Not Found"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    CONFLICT(409, "Conflict"),
    LENGTH_REQUIRED(411, "Length Required");

    private final int code;
    private final String phrase;

    StatusCode(int code, String phrase) {
        this.code = code;
        this.phrase = phrase;
    }

    public int getCode() {
        return code;
    }

    public String getPhrase() {
        return phrase;
    }

    public static StatusCode fromCode(int code) {
        for (StatusCode sc : StatusCode.values()) {
            if (sc.getCode() == code) {
                return sc;
            }
        }
        return null;
    }
}
