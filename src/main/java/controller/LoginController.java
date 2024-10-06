package controller;

import db.MemoryUserRepository;
import http.request.HttpRequest;
import http.response.HttpResponse;
import model.User;

import java.io.IOException;
import java.util.Map;

import static http.request.Url.INDEX_HTML;
import static http.request.Url.USER_LOGIN_FAILED;
import static model.UserQueryKey.PASSWORD;
import static model.UserQueryKey.USERID;


public class LoginController implements Controller {

    MemoryUserRepository memoryUserRepository = MemoryUserRepository.getInstance();

    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {

        Map<String, String> queryParams = httpRequest.getQueryParams();

        //요구사항 5: 로그인하기
        //회원가입한 유저인지 찾기
        User findUser = memoryUserRepository.findUserById(queryParams.get(USERID.getKey()));
        if(findUser == null){
            //회원가입안함
            httpResponse.redirect( USER_LOGIN_FAILED.getPath());
        }
        //로그인 성공
        if(findUser.getPassword().equals(queryParams.get(PASSWORD.getKey())))
        {
            //쿠키 생성 및 리다이렉트
            httpResponse.redirectWithCookie(INDEX_HTML.getPath());
        }
        else {
            // 비밀번호가 틀린 경우
            httpResponse.redirect(USER_LOGIN_FAILED.getPath());
        }

    }
}
