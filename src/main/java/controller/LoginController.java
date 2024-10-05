package controller;

import db.MemoryUserRepository;
import http.util.HttpRequest;
import http.util.HttpResponse;
import model.User;

public class LoginController implements Controller {
    @Override
    public void execute(HttpRequest request, HttpResponse response) throws Exception {
//        String userId = request.getParameter("userId");
//        String password = request.getParameter("password");

        User user = MemoryUserRepository.getInstance().findUserById(userId);
        if (user != null && user.getPassword().equals(password)) {
            response.addHeader("Set-Cookie", "logined=true");
            response.redirect("/index.html");
        } else {
            response.redirect("/user/login_failed.html");
        }
    }
}
