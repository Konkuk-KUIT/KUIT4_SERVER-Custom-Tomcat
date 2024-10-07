package http;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HttpReqStartLineTest {
    @Test
    void StartLine_정상_파싱_test(){
        HttpReqStartLine httpReqStartLine = HttpReqStartLine.from("POST /user/create HTTP/1.1");

        assertEquals("POST", httpReqStartLine.getMethod());
        assertEquals("/user/create", httpReqStartLine.getPath());
        assertEquals("HTTP/1.1", httpReqStartLine.getVersion());
    }

}