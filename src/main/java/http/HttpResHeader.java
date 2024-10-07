package http;

import Constant.HttpHeaderType;

import java.util.HashMap;
import java.util.Map;

public class HttpResHeader {
    private final Map<String, String> headers= new HashMap<>();

    //Headers는 Request에 사용한 것을 재활용할 수 있다. 가 req header의 Map를 받아서 사용하라는 건가? 무슨 의미지?

    public void addHeader(HttpHeaderType header, String message) {
        headers.put(header.getValue(), message);
    }

    // 헤더 출력
    public String toHeaders() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\r\n");
        }
        sb.append("\r\n");              //헤더의 끝을 알리기 위함

        return sb.toString();
    }
}
