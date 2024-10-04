package http.request;

public class HttpRequestStartLine {
    private final String method;
    private final String path;
    private final String version;

    public HttpRequestStartLine(String method, String path, String version) {
        this.method = method;
        this.path = path;
        this.version = version;
    }

    public static HttpRequestStartLine from(String line) {
        String[] tokens = line.split(" ");
        if (tokens.length != 3) {
            throw new IllegalArgumentException("유효하지 않은 값입니다.");
        }
        return new HttpRequestStartLine(tokens[0], tokens[1], tokens[2]);
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getVersion() {
        return version;
    }
}