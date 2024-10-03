package controller;

import http.constant.HttpHeaderType;
import http.constant.HttpURL;
import http.request.HttpRequest;
import http.response.HttpResponse;

import java.io.IOException;

import static http.constant.HttpURL.*;

public class UserListController implements Controller {

    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        if(!request.getHeader(HttpHeaderType.COOKIE).equals("logined=true")){
            response.response302(LOGIN_HTML.getUrl());
            return;
        }
        response.forward(USER_LIST_HTML.getUrl());
    }
}
