package http.request;

public class HttpRequestStartLine {
    private static String method;
    private static String url;
    private HttpRequestStartLine(String method, String url) {
        this.method = method;
        this.url = url;
    }

    public static HttpRequestStartLine from(String startLine) {
        String[] split = startLine.split(" ");
        method = split[0];
        url = split[1];
        System.out.println(url);

        return new HttpRequestStartLine(split[0], split[1]);
    }

    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }
}
