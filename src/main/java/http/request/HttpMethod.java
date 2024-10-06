package http.request;

public enum HttpMethod {
    GET,
    POST;

    public static HttpMethod fromString(String method) {
        switch (method.toUpperCase()) {
            case "GET":
                return GET;
            case "POST":
                return POST;
            default:
                throw new IllegalArgumentException();
        }
    }

    public boolean isEqual(HttpMethod httpMethod) {
        return this == httpMethod;
    }
}