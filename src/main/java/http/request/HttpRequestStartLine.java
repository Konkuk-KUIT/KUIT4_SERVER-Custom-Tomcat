package http.request;

public class HttpRequestStartLine {

    private final HttpMethod method;
    private final String url;
    private final String queryString;


    private HttpRequestStartLine(HttpMethod method, String url, String queryString) {
        this.method = method;
        this.url = url;
        this.queryString = queryString;
    }

    public static HttpRequestStartLine createHttpStartLine(String requestLine){

        if (requestLine == null || requestLine.isEmpty()) {
            return null;
        }
        else{
            String[] requestLines = requestLine.split(" ");
            HttpMethod method = HttpMethod.fromString(requestLines[0]); // 요청 방식 (GET, POST 등)
            String requestedFile = requestLines[1]; // 요청된 파일 (URL)

            // URL과 쿼리 스트링을 분리
            String[] pathAndQuery = requestedFile.split("\\?");
            String url = pathAndQuery[0]; // URL (쿼리 스트링 제외)
            String queryString = pathAndQuery.length > 1 ? pathAndQuery[1] : null; // 쿼리 스트링이 있으면 추출

            return new HttpRequestStartLine(method, url, queryString);
        }

    }

    public String getUrl() {
        return url;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getQueryString() {
        return queryString;
    }
}
