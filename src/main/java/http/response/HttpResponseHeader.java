package http.response;

import java.util.Map;

public class HttpResponseHeader {

    private Map<String, String> headerMap;

    public HttpResponseHeader(Map<String, String> headerMap) {
        this.headerMap = headerMap;
    }

    public Map<String, String> getHeaderMap() {
        return headerMap;
    }

    public void putHeader(String key, String value) {
        headerMap.put(key, value);
    }
}
