package controller;

import http.request.HttpRequest;
import http.response.HttpResponse;

import java.io.IOException;

import static constant.URL.LIST;
import static constant.URL.LOGIN_HTML;

public class UserListController implements Controller {
    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        if (request.isLogin()) {
            response.redirect(LIST.getUrl(), request.isLogin());
            return;
        }
        response.redirect(LOGIN_HTML.getUrl(), request.isLogin());
    }
}
