package http.request;

import http.constants.HttpMethod;
import org.junit.jupiter.api.Test;


import static http.constants.HttpMethod.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HttpRequestStartLineTest {

    @Test
    void 정상_동작() {
        HttpRequestStartLine startLine = HttpRequestStartLine.from("GET ./index.html HTTP/1.1");

        assertThat(startLine.getHttpMethod()).isEqualTo(GET);
        assertThat(startLine.getPath()).isEqualTo("./index.html");
        assertThat(startLine.getVersion()).isEqualTo("HTTP/1.1");
    }

    @Test
    void 쿼리_정상_동작() {
        HttpRequestStartLine startLine = HttpRequestStartLine.from("GET ./index.html?name=query HTTP/1.1");

        assertThat(startLine.getHttpMethod()).isEqualTo(GET);
        assertThat(startLine.getPath()).isEqualTo("./index.html");
        assertThat(startLine.getVersion()).isEqualTo("HTTP/1.1");
        assertThat(startLine.getQueryParameter("name")).isEqualTo("query");
    }

    @Test
    void 지원하지_않는_메소드() {
        assertThrows(IllegalArgumentException.class,()->HttpRequestStartLine.from("PATCH ./index.html HTTP/1.1"));
    }

    @Test
    void 이상한_문장() {
        assertThrows(IllegalArgumentException.class,()->HttpRequestStartLine.from("안녕하세요"));
    }


    @Test
    void 쿼리_존재하지_않을_때() {
        HttpRequestStartLine startLine = HttpRequestStartLine.from("POST /user/create? HTTP/1.1");

        assertThat(startLine.getHttpMethod()).isEqualTo(POST);
        assertThat(startLine.getPath()).isEqualTo("/user/create");
    }
}