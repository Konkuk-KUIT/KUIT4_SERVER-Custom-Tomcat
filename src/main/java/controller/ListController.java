package controller;

import http.request.HttpRequest;
import http.response.HttpResponse;

import java.io.IOException;

import static http.HttpHeader.*;
import static http.Url.*;

public class ListController implements Controller {
    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        String cookie = request.getHeader(COOKIE.getValue());

        if (LOGINED_TRUE.getValue().equals(cookie)) {
            response.forward(USER_LIST_HTML.getUrl());
        } else {
            response.redirect(USER_LOGIN_HTML.getUrl(), null);
        }
    }
}
