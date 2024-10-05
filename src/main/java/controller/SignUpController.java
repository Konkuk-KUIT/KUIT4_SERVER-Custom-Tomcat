package controller;

import db.MemoryUserRepository;
import http.util.HttpRequest;
import http.util.HttpResponse;
import model.User;

public class SignUpController implements Controller {
    @Override
    public void execute(HttpRequest request, HttpResponse response) throws Exception {
//        String userId = request.getParameter("userId");
//        String password = request.getParameter("password");
//        String name = request.getParameter("name");
//        String email = request.getParameter("email");

        if (userId != null && password != null && name != null && email != null) {
            User user = new User(userId, password, name, email);
            MemoryUserRepository.getInstance().addUser(user);
        }

        response.redirect("/index.html");
    }
}
