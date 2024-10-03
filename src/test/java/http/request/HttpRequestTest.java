package http.request;

import http.constants.HttpMethod;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


class HttpRequestTest {
    private final String testDirectory = "./src/test/resources/";
    private final String getPath = "HttpGetWithQuery.txt";
    private final String postPath = "HttpPostWithQuery.txt";

    @Test
    void HTTP_GET_Query() throws IOException {
        HttpRequest httpRequest = HttpRequest.from(bufferedReaderFromFile(testDirectory + getPath));

        assertThat(httpRequest.getUrl()).isEqualTo("/user/create");
        assertThat(httpRequest.getMethod()).isEqualTo(HttpMethod.GET);
        assertThat(httpRequest.getQueryParameter("userId")).isEqualTo("hj");
        assertThat(httpRequest.getQueryParameter("password")).isEqualTo("password");
        assertThat(httpRequest.getQueryParameter("name")).isEqualTo("hyeongju");
    }

    @Test
    void HTTP_POST_Query() throws IOException {
        HttpRequest httpRequest = HttpRequest.from(bufferedReaderFromFile(testDirectory + postPath));
        Map<String, String> queryParametersFromBody = httpRequest.getQueryParametersFromBody();

        assertThat(queryParametersFromBody.get("userId")).isEqualTo("hj");
        assertThat(queryParametersFromBody.get("password")).isEqualTo("password");
        assertThat(queryParametersFromBody.get("name")).isEqualTo("hyeongju");
        assertThat(httpRequest.getUrl()).isEqualTo("/user/create");
        assertThat(httpRequest.getMethod()).isEqualTo(HttpMethod.POST);
    }

    private BufferedReader bufferedReaderFromFile(String path) throws IOException {
        return new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(path))));
    }
}