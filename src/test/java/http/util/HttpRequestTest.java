package http.util;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class HttpRequestTest {

    private BufferedReader bufferedReaderFromFile(String path) throws IOException {
        return new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(path))));
    }

    @Test
    void 시작줄_메서드_확인() throws IOException {
        HttpRequest httpRequest = HttpRequest.from(bufferedReaderFromFile("src/test/resource/message.txt"));
        assertEquals("POST", httpRequest.getMethod());
    }

    @Test
    void 시작줄_요청타깃_확인() throws IOException {
        HttpRequest httpRequest = HttpRequest.from(bufferedReaderFromFile("src/test/resource/message.txt"));
        assertEquals("/user/create", httpRequest.getPath());
    }

    @Test
    void 시작줄_버전_확인() throws IOException {
        HttpRequest httpRequest = HttpRequest.from(bufferedReaderFromFile("src/test/resource/message.txt"));
        assertEquals("HTTP/1.1", httpRequest.getVersion());
    }

    @Test
    void 바디_확인() throws IOException {
        HttpRequest httpRequest = HttpRequest.from(bufferedReaderFromFile("src/test/resource/message.txt"));
        assertEquals("userId=jw&password=password&name=jungwoo", httpRequest.getBody());
    }
}