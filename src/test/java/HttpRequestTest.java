import constant.QueryKey;
import http.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import static constant.QueryKey.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpRequestTest {

    private BufferedReader bufferedReaderFromFile(String path) throws IOException {
        return new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(path))));
    }

    @Test
    @DisplayName("http 요청 받아오기 테스트")
    void http_request_test() throws IOException {
        HttpRequest httpRequest = HttpRequest.from(bufferedReaderFromFile("src/test/resource/" + "test.txt"));

        assertEquals("/user/create", httpRequest.getUrl());
        assertEquals("POST", httpRequest.getMethod());
        assertEquals("HTTP/1.1", httpRequest.getVersion());

        assertEquals("localhost:8080", httpRequest.getHeaderValue("Host"));
        assertEquals("keep-alive", httpRequest.getHeaderValue("Connection"));
        assertEquals("40", httpRequest.getHeaderValue("Content-Length"));
        assertEquals("*/*", httpRequest.getHeaderValue("Accept"));

        assertEquals("jw", httpRequest.getBodyParamValue(USERID.getKey()));
        assertEquals("password", httpRequest.getBodyParamValue(PASSWORD.getKey()));
        assertEquals("jungwoo", httpRequest.getBodyParamValue(NAME.getKey()));

    }
}
