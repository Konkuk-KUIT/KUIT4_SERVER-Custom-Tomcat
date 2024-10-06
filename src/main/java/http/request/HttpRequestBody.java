package http.request;

import http.util.HttpRequestUtils;

import java.util.Map;

public class HttpRequestBody {

    private final Map<String, String> queryParams;

    private HttpRequestBody(Map<String, String> queryParams) {
        this.queryParams = queryParams;
    }

    public static HttpRequestBody CreateRequestBody(String requestBody){

        if (requestBody != null) {
            Map<String, String> queryParams = HttpRequestUtils.parseQueryParameter(requestBody);
            return new HttpRequestBody(queryParams);
        }
        return null;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }
}
