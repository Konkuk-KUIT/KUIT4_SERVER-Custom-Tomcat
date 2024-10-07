package http.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

//HTTP 요청의 쿼리 파라미터를 파싱하는 유틸리티 클래스
public class HttpRequestUtils {
    public static Map<String, String> parseQueryParameter(String queryString) {                 //쿼리 스트링을 파싱하여 키-값 쌍의 Map으로 변환
        try {
            String[] queryStrings = queryString.split("&");

            return Arrays.stream(queryStrings)
                    .map(q -> q.split("="))
                    .collect(Collectors.toMap(queries -> queries[0], queries -> queries[1]));
        } catch (Exception e) {
            return new HashMap<>();
        }
    }
}
