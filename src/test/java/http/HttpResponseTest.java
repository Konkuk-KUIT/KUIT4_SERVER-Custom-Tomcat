package http;

import http.response.HttpResponse;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class HttpResponseTest {

    private OutputStream outputStreamToFile(String path) throws IOException {
        return Files.newOutputStream(Paths.get(path));
    }

    @Test
    void httpResponseTest() throws IOException {
        HttpResponse httpResponse = new HttpResponse(outputStreamToFile("src/test/resources/" + "responseTest.txt"));

        httpResponse.forward("/index.html");
    }
}
