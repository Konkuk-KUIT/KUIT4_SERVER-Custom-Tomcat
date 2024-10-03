package controller;

import db.MemoryUserRepository;
import db.Repository;
import http.request.HttpRequest;
import http.response.HttpResponse;
import model.User;

import java.io.IOException;
import java.util.logging.Level;

import static constant.HttpHeaderTitle.SET_COOKIE;
import static constant.QueryKey.PASSWORD;
import static constant.QueryKey.USERID;
import static constant.Url.INDEX_HTML;
import static constant.Url.USER_LOGIN_FAILED_HTML;

public class LoginController implements Controller{

    Repository repository;

    public LoginController(Repository repository) {
        this.repository = repository;
    }

    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        String userId = request.getBodyParamValue(USERID.getKey());
        String password = request.getBodyParamValue(PASSWORD.getKey());

        User findUser = repository.findUserById(userId);

        // 입력한 ID에 대한 회원 자체가 존재하지 않는 경우
        if (findUser == null) {

            response.putHeader(SET_COOKIE.getHeaderTitle(), "logined=false");
            response.redirect(USER_LOGIN_FAILED_HTML.getUrl());
        } else { // 입력한 ID에 대한 회원은 존재하는 경우

            String findUserPassword = findUser.getPassword();

            if (findUserPassword.equals(password)) {
                response.putHeader(SET_COOKIE.getHeaderTitle(), "logined=true");
                response.redirect(INDEX_HTML.getUrl());
            } else {
                response.putHeader(SET_COOKIE.getHeaderTitle(), "logined=false");
                response.redirect(USER_LOGIN_FAILED_HTML.getUrl());
            }
        }
    }
}
