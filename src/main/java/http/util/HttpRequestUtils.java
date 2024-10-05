package http.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequestUtils {
    public static Map<String, String> parseQueryParameter(String queryString) {
        try {
            String[] queryStrings = queryString.split("&");
            //streamㅇ로 변환
            //=으로 나눠서 분리
            //collect(Collectors.toMap())는 이 스트림을 Map<String, String>으로 변환합니다.
            //queries[0]: key에 해당하는 부분 (userId).
            //queries[1]: value에 해당하는 부분 (admin).
            //이런식으로 분리해서 MAP형태로 넘김
            //userid=1
            return Arrays.stream(queryStrings)
                    .map(q -> q.split("="))
                    .collect(Collectors.toMap(queries -> queries[0], queries -> queries[1]));
        } catch (Exception e) {
            return new HashMap<>();
        }
    }
    public static Map<String, String> parseCookies(String cookieHeader) {
        Map<String, String> cookies = new HashMap<>();
        String[] cookiePairs = cookieHeader.split("; "); // 각 쿠키를 분리
        for (String cookie : cookiePairs) {
            String[] keyValue = cookie.split("="); // "key=value"를 분리
            if (keyValue.length == 2) {
                cookies.put(keyValue[0], keyValue[1]); // 쿠키 이름과 값을 Map에 저장
            }
        }
        return cookies;
    }

}
