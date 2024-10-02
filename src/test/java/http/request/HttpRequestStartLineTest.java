package http.request;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class HttpRequestStartLineTest {
    @Test
    public void startLine_정상_동작(){
        HttpRequestStartLine startLine = HttpRequestStartLine.from("GET /index.html HTTP/1.1");
        assertThat(startLine.method.getMethod()).isEqualTo("GET");
        assertThat(startLine.target).isEqualTo("/index.html");
        assertThat(startLine.version).isEqualTo("HTTP/1.1");

    }
    @Test
    public void startLine_예외(){
        assertThatThrownBy(()-> HttpRequestStartLine.from("GET /index.html"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void startLine_쿼리_정상_동작(){
        HttpRequestStartLine startLine = HttpRequestStartLine.from("GET /index.html?userId=dlwjddus HTTP/1.1");
        assertThat(startLine.method.getMethod()).isEqualTo("GET");
        assertThat(startLine.target).isEqualTo("/index.html");
        assertThat(startLine.version).isEqualTo("HTTP/1.1");
        assertThat(startLine.queryString.get("userId")).isEqualTo("dlwjddus");
    }

    @Test
    public void http_메소드_오류(){
        assertThatThrownBy(() -> HttpRequestStartLine.from("DELETE /index.html HTTP/1.1"))
                .isInstanceOf(IllegalArgumentException.class);
    }

}
