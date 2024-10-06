package HttpRequest;

import java.io.BufferedReader;
import java.io.IOException;

public class HttpRequest {

    private final HttpStartLine httpStartLine;
    private final HttpHeader httpHeader;
    private final HttpBody httpBody;


    private HttpRequest(BufferedReader br) {
        httpStartLine = new HttpStartLine(br);
        httpHeader = new HttpHeader(br);
        httpBody = new HttpBody(br, getContentLength());
    }

    public static HttpRequest from(BufferedReader br) throws IOException {
        return new HttpRequest(br);
    }

    public String getMethod() {
        return httpStartLine.getMethod();
    }

    public String getUrl() {
        return httpStartLine.getUrl();
    }

    public String getVersion() {
        return httpStartLine.getVersion();
    }

    public int getContentLength() {
        return httpHeader.getContentLength();
    }

    public boolean isLogined() {
        return httpHeader.isLogined();
    }

    public String getBody() {
        return httpBody.getBody();
    }
}
