package http.request;

import constant.HttpMethod;

import java.io.BufferedReader;
import java.io.IOException;

import static constant.HttpHeaderTitle.CONTENT_LENGTH;
import static constant.HttpHeaderTitle.COOKIE;
import static constant.HttpMethod.*;
import static constant.Url.CSS_EXTENSION;
import static constant.Url.HTML_EXTENSION;

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

    // GET 요청인지 확인
    public boolean isGetMethod(){
        return httpRequestStartLine.getMethod().equals(GET.getMethod());
    }

    // url이 .html로 끝나는지 확인
    public boolean endsWithHtml(){
        return httpRequestStartLine.getUrl().endsWith(HTML_EXTENSION.getUrl());
    }

    // url이 .css로 끝나는지 확인
    public boolean endsWithCss() {
        return httpRequestStartLine.getUrl().endsWith(CSS_EXTENSION.getUrl());
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
