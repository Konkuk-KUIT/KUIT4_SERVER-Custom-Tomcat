package controller;

import db.MemoryUserRepository;
import http.request.HttpRequest;
import http.response.HttpResponse;
import http.util.HttpRequestUtils;
import model.User;

import java.io.IOException;
import java.util.Map;

public class ListController implements Controller{
    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        String cookie = httpRequest.getCookie(); // 요청된 파일
        if(cookie.contains("logined=true")){
            httpResponse.redirect("/user/list.html");
        }
        else{
            httpResponse.redirect("/user/login.html");
        }
    }
}
