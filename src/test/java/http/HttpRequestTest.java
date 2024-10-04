package http;

import http.request.HttpRequest;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpRequestTest {

    private BufferedReader bufferedReaderFromFile(String path) throws IOException {
        return new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(path))));
    }

    @Test
    void httpRequestTest() throws IOException {
        HttpRequest httpRequest = HttpRequest.from(bufferedReaderFromFile("src/test/resources/" + "requestTest.txt"));

        assertEquals("POST", httpRequest.getMethod());
        assertEquals("/user/create", httpRequest.getPath());

        assertEquals("localhost:8080", httpRequest.getHeader("Host"));
        assertEquals("keep-alive", httpRequest.getHeader("Connection"));
        assertEquals("40", httpRequest.getHeader("Content-Length"));
        assertEquals("*/*", httpRequest.getHeader("Accept"));

        assertEquals("userId=jw&password=password&name=jungwoo", httpRequest.getBody());
    }
}
