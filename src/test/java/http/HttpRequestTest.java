package http;

import org.junit.jupiter.api.Test;
import java.io.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static Constant.UserQueryKey.*;

public class HttpRequestTest {
    @Test
    public void testHttpRequest() throws IOException {
        InputStream is = new FileInputStream("src/test/resources/HttpRequestSample.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        HttpRequest httpRequest = HttpRequest.from(br);

        assertEquals("/user/create", httpRequest.getPath());
        assertEquals("POST", httpRequest.getMethod());
        assertEquals("HTTP/1.1", httpRequest.getVersion());

        assertEquals("localhost:8080", httpRequest.getHeader("Host"));
        assertEquals("keep-alive", httpRequest.getHeader("Connection"));
        assertEquals("40", httpRequest.getHeader("Content-Length"));
        assertEquals("*/*", httpRequest.getHeader("Accept"));

        assertEquals("jw", httpRequest.getBody(USERID.getValue()));
        assertEquals("password", httpRequest.getBody(PASSWORD.getValue()));
        assertEquals("jungwoo", httpRequest.getBody(NAME.getValue()));
    }
}