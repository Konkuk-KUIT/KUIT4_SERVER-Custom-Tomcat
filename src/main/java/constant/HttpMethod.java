package constant;

public enum HttpMethod {

    GET("GET"),
    POST("POST");

    final String method;

    private HttpMethod(String method) {
        this.method = method;
    }

    public String getMethod() {
        return method;
    }
}