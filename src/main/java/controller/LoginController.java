package controller;

import db.MemoryUserRepository;
import http.request.HttpRequest;
import http.response.HttpResponse;
import model.User;

import java.io.IOException;
import java.util.Map;

import static constant.QueryKey.PASSWORD;
import static constant.QueryKey.USER_ID;
import static constant.URL.INDEX;
import static constant.URL.LOGIN_FAILED;

public class LoginController implements Controller {

    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        Map<String, String> loginInfo = request.getQueryParametersfromBody();
        User findUser = findUser(loginInfo);
        if (findUser != null && findUser.getPassword().equals(loginInfo.get(PASSWORD.getKey()))) {
            response.redirect(INDEX.getUrl(), true);
            return;
        }
        response.redirect(LOGIN_FAILED.getUrl(), false);
    }

    private static User findUser(Map<String, String> loginInfo) {
        MemoryUserRepository memoryUserRepository = MemoryUserRepository.getInstance();
        return memoryUserRepository.findUserById(loginInfo.get(USER_ID.getKey()));
    }
}
