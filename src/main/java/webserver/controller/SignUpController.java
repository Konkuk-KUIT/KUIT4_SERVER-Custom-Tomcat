package webserver.controller;

import db.Repository;
import model.User;
import webserver.*;

import java.io.IOException;
import java.util.HashMap;

public class SignUpController implements Controller {
    private Repository userRepository;
    public SignUpController(Repository userRepository) {
        this.userRepository = userRepository;
    }
    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        HashMap<String, String> params = new HashMap<>();
        if (request.getMethod().equals(HttpMethod.GET.getMethod())) {
            // 요구 사항2:  get로 회원가입
            params = (HashMap<String, String>) request.getQueryParams();
        } else if (request.getMethod().equals(HttpMethod.POST.getMethod())) {
            // 요구 사항3:  post로 회원가입
            params = (HashMap<String, String>) request.getBodyParams();
        }
        String userId = params.get(UserQueryKey.USER_ID.getKey());
        String password = params.get(UserQueryKey.PASSWORD.getKey());
        String name = params.get(UserQueryKey.NAME.getKey());
        String email = params.get(UserQueryKey.EMAIL.getKey());
        User user = new User(userId, password, name, email);
        if (userRepository.findUserById(userId) == null) {
            userRepository.addUser(user);
        }
        response.redirect(URLPath.INDEX.getPath());
    }
}
