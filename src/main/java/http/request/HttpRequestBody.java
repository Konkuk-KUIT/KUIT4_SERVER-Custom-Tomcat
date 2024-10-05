package http.request;

import java.io.BufferedReader;
import java.io.IOException;

public class HttpRequestBody {
    private String body;

    public HttpRequestBody(String body) {
        this.body = body;
    }

    public static HttpRequestBody from(BufferedReader br, int contentLength) throws IOException {
        if (contentLength == 0) {
            return new HttpRequestBody(null);
        }

        char[] buffer = new char[contentLength];
        br.read(buffer, 0, contentLength);
        String body = new String(buffer);

        return new HttpRequestBody(body);
    }

    public String getBody() {
        return body;
    }
}
