package http.util;

import http.request.HttpRequest;
import http.response.HttpResponse;
import org.junit.jupiter.api.Test;

import java.io.*;
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
    void HttpRequestTest() throws IOException {

        String testDirectory = "./src/test/resources/";

        HttpRequest httpRequest = HttpRequest.from(bufferedReaderFromFile(testDirectory + "input.txt"));

        assertEquals("/user/create", httpRequest.getUrl());

    }

    private DataOutputStream outputStreamToFile(String path) throws IOException {
        OutputStream outputStream = Files.newOutputStream(Paths.get(path));
        return new DataOutputStream(outputStream);
    }


    @Test
    void HttpResponseTest() throws IOException {

        String testDirectory = "./src/test/resources/";

        HttpResponse httpResponse = new HttpResponse(outputStreamToFile(testDirectory+"output.txt"));

        httpResponse.forward("/index.html");

    }


}