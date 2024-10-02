package http.request;

import http.constant.HttpMethod;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import static http.constant.HttpMethod.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpRequestTest {
    private final String Directory = "./src/test/resource/";
    private final String Path = "RequestTest.txt";
    private BufferedReader bufferedReaderFromFile(String path) throws IOException {
        return new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(path))));
    }
    @Test
    public void HttpRequestTest() throws IOException {
        HttpRequest request = HttpRequest.from(bufferedReaderFromFile(Directory + Path));
        assertThat(request.getHttpMethod()).isEqualTo(POST);
        assertThat(request.getQueryMap().get("userId")).isEqualTo("dlwjddus1112");
    }
}
