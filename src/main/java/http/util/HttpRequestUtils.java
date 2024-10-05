package http.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequestUtils {
    public static Map<String, String> parseQueryParameter(String queryString) {
        try {
            String[] queryStrings = queryString.split("&");

            return Arrays.stream(queryStrings)
                    .map(q -> q.split("="))
                    .collect(Collectors.toMap(queries -> queries[0], queries -> queries[1]));
        } catch (Exception e) {
            return new HashMap<>();
        }
    }
    public static Map<String, String> parseCookies(String cookieHeader) {
        Map<String, String> cookies = new HashMap<>();

        // "key1=value1; key2=value2" 형태의 문자열을 ";"로 분리
        String[] pairs = cookieHeader.split(";");
        for (String pair : pairs) {
            String[] keyValue = pair.trim().split("=");
            if (keyValue.length == 2) {
                String key = keyValue[0].trim();
                String value = keyValue[1].trim();
                cookies.put(key, value);
            }
        }

        return cookies;
    }
}
