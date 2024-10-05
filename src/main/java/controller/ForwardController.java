package controller;


import http.request.HttpRequest;
import http.response.HttpResponse;

import java.io.IOException;

import static constant.Url.*;

public class ForwardController implements Controller {

    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {

        // form.html, login.html, login_failed.html 에서는 list.html 에 forward 방식으로 바로 접근한다. 따라서 검증 로직을
        // 이 위치에도 한 번 더 적용해주었다.
        if (request.getUrl().equals(USER_LIST_HTML.getUrl())) {
            boolean login = request.checkLogin();

            if (login) {
                response.forward(USER_LIST_HTML.getUrl());
                return;
            }

            response.redirect(USER_LOGIN_HTML.getUrl());
            return;

        }

        response.forward(request.getUrl());
    }

}
