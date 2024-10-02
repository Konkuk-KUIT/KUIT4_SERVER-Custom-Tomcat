package http;

import http.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class HttpResponseTest {

    private OutputStream outputStreamToFile(String path) throws IOException {
        return Files.newOutputStream(Paths.get(path));
    }

    @Test
    @DisplayName("http 응답 출력하기 테스트")
    void http_response_test() throws IOException {

        HttpResponse httpResponse = new HttpResponse(outputStreamToFile("src/test/resource/" + "responseTest.txt"));

        httpResponse.forward("/index.html");
    }
}
