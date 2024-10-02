package http;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static constant.HttpHeaderTitle.CONTENT_LENGTH;
import static constant.HttpHeaderTitle.COOKIE;

public class HttpRequest {

    private HttpStartLine httpStartLine;
    private HttpHeader httpHeader;
    private Body body;

    private HttpRequest(BufferedReader br) throws IOException {
        httpStartLine = HttpStartLine.from(br);
        httpHeader = HttpHeader.from(br);
        body = Body.from(br);

        httpHeader.parseHeader();

        if (httpHeader.containsKey(CONTENT_LENGTH.getHeaderTitle())) {
            body.setBody(Integer.parseInt(httpHeader.getValue(CONTENT_LENGTH.getHeaderTitle())));
            body.parseBody();
        }

    }

    public static HttpRequest from(BufferedReader br) throws IOException {
        return new HttpRequest(br);
    }

    public String getMethod() {
        return httpStartLine.getMethod();
    }

    public String getUrl() {
        return httpStartLine.getUrl();
    }

    public String getVersion() {
        return httpStartLine.getVersion();
    }

    public String getHeaderValue(String key) {
        return httpHeader.getValue(key);
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
        return body.getBodyParamValue(key);
    }

    // queryString을 통해 들어온 queryparameter들을 파싱한 맵에서 key에 대응되는 value를 가져옴
    public String getQueryParamValue(String key) {
        return httpStartLine.getQueryParamValue(key);
    }



}
