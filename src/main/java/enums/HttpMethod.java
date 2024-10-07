package enums;

public enum HttpMethod {
    GET,
    POST,
    PUT,
    DELETE;
    public String getMethod() {
        return this.toString();
    }
}

