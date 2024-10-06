package controller;

import db.MemoryUserRepository;
import http.request.HttpRequest;
import http.response.HttpResponse;
import http.util.HttpRequestUtils;
import model.User;

import java.io.IOException;
import java.util.Map;

import static http.HttpHeaders.LOGINED_TRUE;
import static http.request.Url.USER_LIST_HTML;
import static http.request.Url.USER_LOGIN_HTML;

public class ListController implements Controller{
    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException{

        //요구사항 6: 사용자 목록 출력
        String cookie = httpRequest.getCookie();
        if(cookie.contains(LOGINED_TRUE.getHeader())){
            httpResponse.redirect(USER_LIST_HTML.getPath());
        }
        else{
            httpResponse.redirect(USER_LOGIN_HTML.getPath());
        }
    }
}
