package webserver.controller;

import db.Repository;
import model.User;
import webserver.HttpRequest;
import webserver.HttpResponse;
import webserver.URLPath;

import java.io.IOException;


public class LoginController implements Controller {
    private Repository userRepository;
    public LoginController(Repository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        String userId = request.getBodyParams().get("userId");
        String password = request.getBodyParams().get("password");
        User user = userRepository.findUserById(userId);
        final boolean authenticated = user != null && password.equals(user.getPassword());
        if (authenticated){
            response.addHeader("Set-Cookie", "logined=true");
            response.forward(URLPath.INDEX.getPath());
        }
        response.redirect(URLPath.LOGINFAIL.getPath());
    }
}
