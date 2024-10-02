package webserver.controller;

import db.Repository;
import model.User;
import webserver.HttpRequest;
import webserver.HttpResponse;
import webserver.URLPath;

import java.io.IOException;


public class LoginController implements Controller {
    private Repository userRepository;
    public void setUserRepository(Repository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void execute(HttpRequest request, HttpResponse response) {
        String userId = request.getBodyParams().get("userId");
        String password = request.getBodyParams().get("password");
        User user = userRepository.findUserById(userId);
        if (user != null){
            if (password.equals(user.getPassword())){
                //todo 아직 쿠키 res에 대해선 리펙토링 안함.
                response.response302RedirectWithCookie(URLPath.INDEX.getPath(),"logined=true");
            }
        }
        try {
            response.redirect(URLPath.LOGINFAIL.getPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
