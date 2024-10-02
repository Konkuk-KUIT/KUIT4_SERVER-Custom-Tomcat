package http.request;

import http.util.HttpRequestUtils;
import http.util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RequestBody {

    String body;
    Map<String, String> bodyMap;
    BufferedReader br;

    private RequestBody(BufferedReader br) throws IOException {
        this.br = br;
        bodyMap = new HashMap<>();
    }

    public static RequestBody from(BufferedReader br) throws IOException {
        return new RequestBody(br);
    }

    public void setBody(int requestContentLength) throws IOException {
       body = IOUtils.readData(br, requestContentLength);
    }

    public void parseBody(){
        String queryString = body.substring(body.indexOf("?") + 1);

        bodyMap = HttpRequestUtils.parseQueryParameter(queryString);
    }

    public String getBodyParamValue(String key) {
        return bodyMap.get(key);
    }
}