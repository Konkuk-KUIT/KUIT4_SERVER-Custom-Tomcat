package http.request;


import constant.QueryKey;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import static constant.QueryKey.*;
import static org.assertj.core.api.Assertions.*;


class HttpRequestTest {

    final String testDirectory = "./src/test/resources/";
    String httpRequestPath = "httpRequest.txt";

    private BufferedReader bufferedReaderFromFile(String path) throws IOException {
        return new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(path))));
    }
    
    @Test
    public void HttpRequestStartLineTest() throws Exception {
        //given
        HttpRequest httpRequest = HttpRequest.from(bufferedReaderFromFile(testDirectory + httpRequestPath));

        //when

        //then
        assertThat(httpRequest.isPostMethod()).isTrue();
        assertThat(httpRequest.getUrl()).isEqualTo("/user/signup");
    }

    @Test
    public void HttpRequestHeaderTest() throws Exception {
        //given
        HttpRequest httpRequest = HttpRequest.from(bufferedReaderFromFile(testDirectory + httpRequestPath));

        //when

        //then
        assertThat(httpRequest.getContentLength()).isEqualTo(66);
        assertThat(httpRequest.isLogin()).isFalse();
    }

    @Test
    public void HttpRequestBodyTest() throws Exception {
        //given
        HttpRequest httpRequest = HttpRequest.from(bufferedReaderFromFile(testDirectory + httpRequestPath));

        //when
        Map<String, String> queryParameters = httpRequest.getQueryParametersfromBody();

        //then
        assertThat(queryParameters.get(USER_ID.getKey())).isEqualTo("jw");
        assertThat(queryParameters.get(PASSWORD.getKey())).isEqualTo("password");
        assertThat(queryParameters.get(NAME.getKey())).isEqualTo("jungwoo");
        assertThat(queryParameters.get(EMAIL.getKey())).isEqualTo("buzz03312@naver.com");
    }

}