package controller;

import db.MemoryUserRepository;
import http.request.HttpRequest;
import http.response.HttpResponse;
import model.User;

import java.io.IOException;
import java.util.Map;

import static constant.QueryKey.*;
import static constant.QueryKey.EMAIL;
import static constant.URL.INDEX;

public class SignUpController implements Controller {

    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        createNewUser(request.getQueryParameters());
        response.redirect(INDEX.getUrl(), request.isLogin());
    }

    private void createNewUser(Map<String, String> queryParameter) {
        //새로운 User 객체 생성 후 Repository에 추가
        User newUser = new User(queryParameter.get(USER_ID.getKey()), queryParameter.get(PASSWORD.getKey()), queryParameter.get(NAME.getKey()), queryParameter.get(EMAIL.getKey()));
        MemoryUserRepository memoryUserRepository = MemoryUserRepository.getInstance();
        memoryUserRepository.addUser(newUser);
//        System.out.println("newUser_name = " + memoryUserRepository.findUserById(queryParameter.get("userId")).getName());
    }
}
