package controller;

import db.MemoryUserRepository;
import http.request.HttpRequest;
import http.response.HttpResponse;
import model.User;

import java.io.IOException;
import java.util.Map;


public class LoginController implements Controller {

    MemoryUserRepository memoryUserRepository = MemoryUserRepository.getInstance();

    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {

        Map<String, String> queryParams = httpRequest.getQueryParams();

        //회원가입한 유저인지 찾기
        User findUser = memoryUserRepository.findUserById(queryParams.get("userId"));
        if(findUser == null){
            //회원가입안함
            httpResponse.redirect("/user/login_failed.html");
        }
        //로그인 성공
        if(findUser.getPassword().equals(queryParams.get("password")))
        {
            //쿠키 생성 및 리다이렉트
            httpResponse.redirectWithCookie("/index.html");
        }
        else {
            // 비밀번호가 틀린 경우
            httpResponse.redirect("/user/login_failed.html");
        }

    }
}
