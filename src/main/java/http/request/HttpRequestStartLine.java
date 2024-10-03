package http.request;

public class HttpStartLine {

    private final String method;
    private final String url;
    private final String queryString;


    private HttpStartLine(String method, String url, String queryString) {
        this.method = method;
        this.url = url;
        this.queryString = queryString;
    }

    public static HttpStartLine createHttpStartLine(String requestLine){

        if (requestLine == null || requestLine.isEmpty()) {
            return null;
        }
        else{
            String[] requestLines = requestLine.split(" ");
            String method = requestLines[0]; // 요청 방식
            String requestedFile = requestLines[1]; // 요청된 파일
            String[] pathAndQuery = requestedFile.split("\\?");
            String queryString = pathAndQuery.length > 1 ? pathAndQuery[1] : null; // 쿼리 스트링이 있으면 추출
            return new HttpStartLine(method,requestedFile,queryString);
        }

    }

    public String getUrl() {
        return url;
    }

    public String getMethod() {
        return method;
    }

    public String getQueryString() {
        return queryString;
    }
}
