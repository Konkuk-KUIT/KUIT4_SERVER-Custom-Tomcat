package controller;

import db.MemoryUserRepository;
import http.HttpRequest;
import http.HttpResponse;
import http.util.HttpRequestUtils;
import model.User;

import java.io.IOException;
import java.util.Map;

public class SignUpController implements Controller {
    private final MemoryUserRepository userRepository = MemoryUserRepository.getInstance();

    @Override
    public void execute(HttpRequest request, HttpResponse response) {
        try {
            Map<String, String> params = HttpRequestUtils.parseQueryParameter(request.getBody());
            User user = new User(params.get("userId"), params.get("password"), params.get("name"), params.get("email"));
            userRepository.addUser(user);
            // response.addHeader("Set-Cookie", "logined=true; Path=/; Expires=Thu, 01 Jan 1970 00:00:00 GMT; HttpOnly");
            response.redirect("/index.html");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}