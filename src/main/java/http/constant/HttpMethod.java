package http.constant;

public enum HttpMethod {
    GET("GET"),
    POST("POST");

    String method;

    HttpMethod(String method) {
        this.method = method;
    }

    public String getMethod() {
        return method;
    }
}
