package controller;

import db.MemoryUserRepository;
import http.constant.HttpURL;
import http.request.HttpRequest;
import http.response.HttpResponse;
import http.util.HttpRequestUtils;
import model.User;
import model.UserQueryKey;

import java.io.IOException;
import java.util.Map;

import static http.constant.HttpURL.*;
import static model.UserQueryKey.*;

public class SignUpController implements Controller {
    private final MemoryUserRepository userRepository = MemoryUserRepository.getInstance();

    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        Map<String, String> query = request.getQueryFromBody();
        User user = new User(query.get(ID.getKey()), query.get(PWD.getKey()), query.get(NAME.getKey()), query.get(EMAIL.getKey()));
        userRepository.addUser(user);
        response.response302(INDEX.getUrl());
    }
}
