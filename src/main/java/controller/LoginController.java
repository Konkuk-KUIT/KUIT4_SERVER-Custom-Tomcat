package controller;

import db.MemoryUserRepository;
import model.User;
import webserver.*;
import java.io.IOException;
import java.util.Map;

public class LoginController implements Controller{
    @Override
    public void execute(Request request, Response response) throws IOException {

        Map<String, String> queryParams = request.getBodyParams();
        String userId = queryParams.get(UserQueryKey.USER_ID.getKey());
        String password = queryParams.get(UserQueryKey.PASSWORD.getKey());

        User user = MemoryUserRepository.getInstance().findUserById(userId); //findUser 여기서 user 받기

        if(user.getPassword().equals(password)) {
            response.addCookie("logined=true");
            response.redirect("/index.html");
        }else{
            response.redirect("/user/login_failed.html");
        }
    }

}
