package webserver;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


class HttpRequestTest {

    @Test
    void testParseGetRequest() throws IOException {
        String requestString = "GET /index.html?param1=value1&param2=value2 HTTP/1.1\r\n" +
                "Host: localhost:8080\r\n" +
                "\r\n";
        BufferedReader reader = new BufferedReader(new StringReader(requestString));

        HttpRequest request = new HttpRequest(reader);

        assertThat(request.getMethod()).isEqualTo("GET");
        assertThat(request.getPath()).isEqualTo("/index.html?param1=value1&param2=value2");
        assertThat(request.getHeader("Host")).isEqualTo("localhost:8080");

        Map<String, String> params = request.parseQueryString();
        assertThat(params)
                .containsEntry("param1", "value1")
                .containsEntry("param2", "value2");
    }

    @Test
    void testParsePostRequest() throws IOException {
        String requestString = "POST /user/signup HTTP/1.1\r\n" +
                "Host: localhost:8080\r\n" +
                "Content-Type: application/x-www-form-urlencoded\r\n" +
                "Content-Length: 93\r\n" +
                "\r\n" +
                "userId=rbqks529&password=qwer1234&name=%EC%A1%B0%EA%B7%9C%EB%B9%88&email=rbqks529%40naver.com";

        BufferedReader reader = new BufferedReader(new StringReader(requestString));
        HttpRequest request = new HttpRequest(reader);

        assertThat(request.getMethod()).isEqualTo("POST");
        assertThat(request.getPath()).isEqualTo("/user/signup");
        assertThat(request.getHeader("Host")).isEqualTo("localhost:8080");
        assertThat(request.getHeader("Content-Length")).isEqualTo("93");

        Map<String, String> params = request.parseQueryString();
        assertThat(params)
                .containsEntry("userId", "rbqks529")
                .containsEntry("password", "qwer1234")
                .containsEntry("name", "%EC%A1%B0%EA%B7%9C%EB%B9%88")
                .containsEntry("email", "rbqks529%40naver.com");
    }
}