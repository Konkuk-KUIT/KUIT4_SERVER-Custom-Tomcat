package controller;

import db.MemoryUserRepository;
import http.HttpRequest;
import http.HttpResponse;
import http.util.HttpRequestUtils;
import model.User;

import java.io.IOException;
import java.util.Map;

public class LoginController implements Controller {
    private final MemoryUserRepository userRepository = MemoryUserRepository.getInstance();

    @Override
    public void execute(HttpRequest request, HttpResponse response) {
        try {
            Map<String, String> params = HttpRequestUtils.parseQueryParameter(request.getBody());
            User user = userRepository.findUserById(params.get("userId"));
            if (user != null && params.get("password").equals(user.getPassword())) {
                response.addHeader("Set-Cookie", "logined=true; Path=/; Max-Age=30; HttpOnly");
                response.redirect("/index.html");
            } else {
                response.redirect("/user/login_failed.html");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}