package http.request;

import http.util.HttpRequestUtils;

import java.util.Map;

public class RequestBody {

    private final Map<String, String> queryParams;

    private RequestBody(Map<String, String> queryParams) {
        this.queryParams = queryParams;
    }

    public static RequestBody CreateRequestBody(String requestBody){

        if (requestBody != null) {
            Map<String, String> queryParams = HttpRequestUtils.parseQueryParameter(requestBody);
            return new RequestBody(queryParams);
        }
        return null;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }
}
