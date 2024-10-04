package http.util;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RequestHandlerTest {
    //요구사항 1번 구현하기
    @Test
    void RequestIndexHtml() {
        Map<String, String> queryParameter = HttpRequestUtils.parseQueryParameter("userId=1&password=1");

        assertEquals("1",queryParameter.get("password"));
    }

}
