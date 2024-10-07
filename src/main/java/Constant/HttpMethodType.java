package Constant;

public enum HttpMethodType {
    GET("GET"),
    POST("POST");

    final String message;

    HttpMethodType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
