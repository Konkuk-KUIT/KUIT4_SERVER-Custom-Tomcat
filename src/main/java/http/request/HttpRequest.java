package http.request;

import java.io.BufferedReader;
import java.io.IOException;

import static constant.HttpHeaderTitle.CONTENT_LENGTH;
import static constant.HttpHeaderTitle.COOKIE;

public class HttpRequest {

    private HttpRequestStartLine httpRequestStartLine;
    private HttpRequestHeader httpRequestHeader;
    private RequestBody requestBody;

    private HttpRequest(BufferedReader br) throws IOException {
        httpRequestStartLine = HttpRequestStartLine.from(br);
        httpRequestHeader = HttpRequestHeader.from(br);
        requestBody = RequestBody.from(br);

        httpRequestHeader.parseHeader();

        if (httpRequestHeader.containsKey(CONTENT_LENGTH.getHeaderTitle())) {
            requestBody.setBody(Integer.parseInt(httpRequestHeader.getValue(CONTENT_LENGTH.getHeaderTitle())));
            requestBody.parseBody();
        }

    }

    public static HttpRequest from(BufferedReader br) throws IOException {
        return new HttpRequest(br);
    }

    public String getMethod() {
        return httpRequestStartLine.getMethod();
    }

    public String getUrl() {
        return httpRequestStartLine.getUrl();
    }

    public String getVersion() {
        return httpRequestStartLine.getVersion();
    }

    public String getHeaderValue(String key) {
        return httpRequestHeader.getValue(key);
    }

    public boolean checkLogin() {
        boolean login = false;

        if (getHeaderValue(COOKIE.getHeaderTitle()) != null && getHeaderValue(COOKIE.getHeaderTitle()).equals("logined=true")) {
            login = true;
        }

        if (getHeaderValue(COOKIE.getHeaderTitle()) == null || getHeaderValue(COOKIE.getHeaderTitle()).equals("logined=false")) {
            login = false;
        }

        return login;
    }

    // body를 통해 들어온 queryParameter들을 파싱한 맵에서 key에 대응되는 value를 가져옴
    public String getBodyParamValue(String key) {
        return requestBody.getBodyParamValue(key);
    }

    // queryString을 통해 들어온 queryparameter들을 파싱한 맵에서 key에 대응되는 value를 가져옴
    public String getQueryParamValue(String key) {
        return httpRequestStartLine.getQueryParamValue(key);
    }



}
