package controller;

import constants.HttpHeader;
import constants.StatusCode;
import constants.Url;
import db.MemoryUserRepository;
import http.HttpRequest;
import http.HttpResponse;
import http.util.HttpRequestUtils;
import model.User;

import java.io.IOException;
import java.util.Map;

public class SignUpController implements Controller {
    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        // 요청 본문을 파싱하여 회원 정보 추출
        String body = request.getBody();
        Map<String, String> params = HttpRequestUtils.parseQueryParameter(body);

        String userId = params.get("userId");
        String password = params.get("password");
        String name = params.get("name");
        String email = params.get("email");

        // 회원가입 처리 로직
        MemoryUserRepository repository = MemoryUserRepository.getInstance();
        User newUser = new User(userId, password, name, email);  // 올바른 인자를 전달하여 User 객체 생성
        repository.addUser(newUser);

        // 응답 설정: 회원가입 후 메인 페이지로 리디렉션
        response.redirect(Url.INDEX.getPath());
        response.forward();
    }
}