package constant;

public enum HttpMethod {

    GET("GET"),
    POST("POST");

    final String value;

    private HttpMethod(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}