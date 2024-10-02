package controller;

import http.request.HttpRequest;
import http.response.HttpResponse;

import java.io.IOException;

import static constant.Url.USER_LIST_HTML;
import static constant.Url.USER_LOGIN_HTML;

public class ListController implements Controller {
    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        boolean login = request.checkLogin();

        if (login) {
            response.redirect(USER_LIST_HTML.getUrl());
        } else {
            response.redirect(USER_LOGIN_HTML.getUrl());
        }
    }
}
