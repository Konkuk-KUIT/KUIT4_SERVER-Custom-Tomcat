package http.util;

import http.HttpRequest;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpRequestUtilsTest {

    @Test
    void parseQuery() {
        Map<String, String> queryParameter = HttpRequestUtils.parseQueryParameter("userId=1");
        assertEquals("1",queryParameter.get("userId"));
    }

    @Test
    void parseQueryMore() {
        Map<String, String> queryParameter = HttpRequestUtils.parseQueryParameter("userId=1&password=1");
        assertEquals("1",queryParameter.get("userId"));
        assertEquals("1",queryParameter.get("password"));
    }

    @Test
    void parseQueryZero() {
        Map<String, String> queryParameter = HttpRequestUtils.parseQueryParameter("");
    }


    private BufferedReader bufferedReaderFromFile(String path) throws IOException {
        return new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(path))));
    }

    @Test
    void HttpRequest_버퍼리더확인() throws IOException {

        String testDirectory = "./src/test/resources/";

        HttpRequest httpRequest = HttpRequest.from(bufferedReaderFromFile(testDirectory + "test.txt"));

        assertEquals("/user/create", httpRequest.getUrl());

    }


}