package controller;

import constants.HttpHeader;
import constants.StatusCode;
import constants.Url;
import http.HttpRequest;
import http.HttpResponse;
import model.User;
import db.MemoryUserRepository;
import http.util.HttpRequestUtils;

import java.io.IOException;

public class LoginController implements Controller {

    private final MemoryUserRepository userRepository = MemoryUserRepository.getInstance();

    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        // 요청 본문에서 로그인 폼 데이터 추출
        String requestBody = request.getBody();
        String userId = HttpRequestUtils.parseQueryParameter(requestBody).get("userId");
        String password = HttpRequestUtils.parseQueryParameter(requestBody).get("password");

        // 사용자 인증
        User user = userRepository.findUserById(userId);

        if (user != null && user.getPassword().equals(password)) {
            // 로그인 성공 -> 쿠키 설정하고 홈으로 리디렉션
            response.addHeader(HttpHeader.SET_COOKIE, "logined=true; Path=/; HttpOnly");
            response.setStatusCode(StatusCode.FOUND);
            response.addHeader(HttpHeader.LOCATION, Url.INDEX.getPath());
            response.forward();
        } else {
            // 로그인 실패 -> 로그인 실패 페이지로 리디렉션
            response.setStatusCode(StatusCode.FOUND);
            response.addHeader(HttpHeader.LOCATION, Url.LOGIN_FAILED.getPath());
            response.forward();
        }
    }
}