package http;

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
    void http_response_test() throws IOException {

        HttpResponse httpResponse = new HttpResponse(outputStreamToFile("src/test/resources/" + "responseSample.txt"));

        httpResponse.forward("/index.html");
    }
}