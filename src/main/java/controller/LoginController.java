package controller;

import db.MemoryUserRepository;
import http.request.HttpRequest;
import http.response.HttpResponse;
import http.util.HttpRequestUtils;
import model.User;

import java.io.IOException;
import java.util.Map;

import static http.HttpHeader.*;
import static http.Url.*;
import static model.UserQueryKey.*;

public class LoginController implements Controller {
    private final MemoryUserRepository memoryUserRepository = MemoryUserRepository.getInstance();

    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        String body = request.getBody();
        Map<String, String> params = HttpRequestUtils.parseQueryParameter(body);

        String userId = params.get(USER_ID.getKey());
        String password = params.get(PASSWORD.getKey());

        if (userId == null || password == null) {
            response.redirect(USER_LOGIN_FAILED.getUrl(), LOGINED_FALSE.getValue());
            return;
        }

        User user = memoryUserRepository.findUserById(userId);

        if (user != null && user.getPassword().equals(password)) {
            response.redirect(INDEX.getUrl(), LOGINED_TRUE.getValue());
        } else {
            response.redirect(USER_LOGIN_FAILED.getUrl(), LOGINED_FALSE.getValue());
        }
    }
}
