package webserver.enums;

public enum HttpMethod {
    HTTP_GET("GET"),
    HTTP_POST("POST"),
    // 그냥 추가 해봄
    HTTP_PUT("PUT"),
    HTTP_DELETE("DELETE");

    private final String value;

    HttpMethod(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
