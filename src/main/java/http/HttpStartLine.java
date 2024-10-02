package http;

import http.util.HttpRequestUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpStartLine {

    private String method;
    private String url;
    private String version;

    private Map<String, String> queryParamMap;

    private BufferedReader br;

    private HttpStartLine(BufferedReader br) throws IOException {
        this.br = br;
        queryParamMap = new HashMap<>();

        String requestLine = br.readLine();

        String[] requestParts = requestLine.split(" ");

        method = requestParts[0];
        url = requestParts[1];
        version = requestParts[2];

        if(url.contains("?")) {
            String queryString = url.substring(url.indexOf("?") + 1);
            queryParamMap = HttpRequestUtils.parseQueryParameter(queryString);
        }
    }

    public static HttpStartLine from(BufferedReader br) throws IOException {
        return new HttpStartLine(br);
    }

    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public String getVersion() {
        return version;
    }


    // url에 연달아서 ?를 통해 표현되는 queryString을 parsing하여 만든 맵에서 key에 매핑되어있는 value값을 얻어옴
    public String getQueryParamValue(String key) {
        return queryParamMap.get(key);
    }
}