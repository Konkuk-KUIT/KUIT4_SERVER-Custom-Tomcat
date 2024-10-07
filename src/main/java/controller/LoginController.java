package controller;

import db.MemoryUserRepository;
import http.constant.HttpHeaderType;
import http.request.HttpRequest;
import http.response.HttpResponse;
import model.User;
import model.UserQueryKey;

import java.io.IOException;
import java.util.Map;

import static http.constant.HttpHeaderType.*;
import static http.constant.HttpURL.*;
import static model.UserQueryKey.*;

public class LoginController implements Controller {

    private final MemoryUserRepository userRepository = MemoryUserRepository.getInstance();
    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        Map<String, String> queryMap = request.getQueryFromBody();
        User user = userRepository.findUserById(queryMap.get(ID.getKey()));
        String inputId = queryMap.get(ID.getKey());
        String inputPW = queryMap.get(PWD.getKey());
        if(user != null && inputId.equals(user.getUserId()) && inputPW.equals(user.getPassword())){
            response.put(SET_COOKIE,"logined=true");
            response.response302(INDEX.getUrl());
            return;
        }
        response.response302(LOGIN_FAILED.getUrl());

    }

}
