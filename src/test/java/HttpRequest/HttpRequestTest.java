package HttpRequest;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class HttpRequestTest {

    private BufferedReader bufferedReaderFromFile(String path) throws IOException {
        return new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(path))));
    }

    @Test
    void getUrl() throws IOException {
        HttpRequest httpRequest = HttpRequest.from(bufferedReaderFromFile("src/test/resource/input.txt"));
        assertEquals("/user/create", httpRequest.getUrl());
    }

    @Test
    void getMethod() throws IOException {
        HttpRequest httpRequest = HttpRequest.from(bufferedReaderFromFile("src/test/resource/input.txt"));
        assertEquals("POST", httpRequest.getMethod());
    }

    @Test
    void getVersion() throws IOException {
        HttpRequest httpRequest = HttpRequest.from(bufferedReaderFromFile("src/test/resource/input.txt"));
        assertEquals("HTTP/1.1", httpRequest.getVersion());
    }

    @Test
    void getContentLength() throws IOException {
        HttpRequest httpRequest = HttpRequest.from(bufferedReaderFromFile("src/test/resource/input.txt"));
        assertEquals(40, httpRequest.getContentLength());
    }

    @Test
    void getBody() throws IOException {
        HttpRequest httpRequest = HttpRequest.from(bufferedReaderFromFile("src/test/resource/input.txt"));
        assertEquals("userId=jw&password=password&name=jungwoo", httpRequest.getBody());
    }
}