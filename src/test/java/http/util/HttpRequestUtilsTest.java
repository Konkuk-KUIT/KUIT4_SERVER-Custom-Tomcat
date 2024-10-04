package http.util;

import org.junit.jupiter.api.Test;
import webserver.HttpMethod;
import webserver.HttpRequest2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.http.HttpRequest;
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
    @Test
    public void testHttpRequestParsing() throws IOException {
        // resources 폴더에서 test_request.txt 파일을 읽어오기
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("test_request.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

        // HttpRequest 객체 생성
        HttpRequest2 httpRequest = HttpRequest2.from(br);

        // Assert statements to verify the parsing logic
        assertEquals(HttpMethod.POST, httpRequest.getMethod());
        assertEquals("/user/create", httpRequest.getPath());
    }

}