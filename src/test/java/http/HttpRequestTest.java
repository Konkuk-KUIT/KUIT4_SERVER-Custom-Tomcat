package http;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpRequestTest {
    HttpRequest httpRequest;

    @BeforeEach
    void setHttpRequestFromFile() throws IOException {
        String filePath = "src/test/java/resource/httpRequestPostExample";
        BufferedReader br = new BufferedReader(new FileReader(filePath));

        httpRequest = HttpRequest.from(br);
    }

    @Test
    void getHttpMethod() {
        //when
        String actualMethod = httpRequest.getHttpMethod();

        //given
        String expectedMethod = "POST";

        //then
        assertEquals(actualMethod, expectedMethod);
    }

    @Test
    void getUrl() {
        //when
        String actualUrl = httpRequest.getUrl();

        //given
        String expectedUrl = "/user/test";

        //then
        assertEquals(actualUrl, expectedUrl);
    }

    @Test
    void getQueryMap() {
        //when
        Map<String, String> actualQueryMap = httpRequest.getQueryMap();

        //given
        Map<String, String> expectedQueryMap = Map.of(
                "userId", "query_id",
                "password", "query_password"
        );

        //then
        assertEquals(actualQueryMap, expectedQueryMap);
    }

    @Test
    void getHeader() {
        //when
        Map<String, String> actualHeaderMap = httpRequest.getHeader();

        //given
        Map<String, String> expectedHeaderMap = Map.of(
                "Host", "localhost:80",
                "Connection", "keep-alive",
                "Content-Length", "52",
                "Accept", "*/*"
        );

        //then
        assertEquals(actualHeaderMap, expectedHeaderMap);
    }

    @Test
    void getBody() {
        //when
        Map<String, String> actualBodyMap = httpRequest.getBody();

        //given
        Map<String, String> expectedBodyMap = Map.of(
                "userId", "body_id",
                "password", "body_password",
                "name", "body_name"
        );

        //then
        assertEquals(actualBodyMap, expectedBodyMap);
    }
}