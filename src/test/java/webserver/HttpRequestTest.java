package webserver;

import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpRequestTest {
    @Test
    public void testHttpRequest() throws IOException {
        InputStream is = new FileInputStream(new File("src/test/resources/HttpRequestTest.txt"));
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        HttpRequest request = HttpRequest.from(br);

        assertEquals("POST", request.getMethod());
        assertEquals("/user/create", request.getPath());
        assertEquals("HTTP/1.1", request.getVersion());
        assertEquals("40", request.getHeaders().get("Content-Length"));
        assertEquals("jungwoo", request.getBodyParams().get("name"));
    }

}
