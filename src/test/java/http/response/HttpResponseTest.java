package http.response;

import http.constant.HttpStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import static http.constant.HttpStatus.*;
import static org.assertj.core.api.Assertions.*;

public class HttpResponseTest {
    final String directory = "./src/test/resource/";
    final String path = "ResponseTest.txt";

    @Test
    void HttpResponse_정상_동작() throws IOException {
        HttpResponse httpResponse = new HttpResponse(outputStreamToFile(directory+path));
        httpResponse.forward("/index.html");
        assertThat(httpResponse.getStartLine().getStatusCode()).isEqualTo("200");
        assertThat(httpResponse.getStartLine().getVersion()).isEqualTo("HTTP/1.1");
        assertThat(httpResponse.getStartLine().getHttpStatus()).isEqualTo(OK);

    }

    @Test
    void HttpResponse_redirect() throws IOException{
        HttpResponse httpResponse = new HttpResponse(outputStreamToFile(directory+path));
        httpResponse.response302("/index.html");
    }





    private OutputStream outputStreamToFile(String path) throws IOException {
        return Files.newOutputStream(Paths.get(path));
    }
}
