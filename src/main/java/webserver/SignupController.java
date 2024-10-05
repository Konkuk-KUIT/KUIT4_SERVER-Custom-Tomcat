package webserver;

import db.MemoryUserRepository;
import http.util.HttpRequestUtils;
import model.User;

import java.io.IOException;
import java.util.Map;

public class SignupController implements Controller{
    @Override
    public void execute(HttpResponse httpResponse, HttpRequest2 httpRequest) throws IOException {
        Map<String, String> queryParms = HttpRequestUtils.parseQueryParameter(httpRequest.getBody());

        String userId = queryParms.get(UserQueryKey.USER_ID.getKey());
        String password = queryParms.get(UserQueryKey.PASSWORD.getKey());
        String name = queryParms.get(UserQueryKey.NAME.getKey());
        String email = queryParms.get(UserQueryKey.EMAIL.getKey());

        User newUser = new User(userId, password, name, email);

        // 유저 저장
        MemoryUserRepository.getInstance().addUser(newUser);
        // 홈으로 리다이렉트
        httpResponse.redirect("/");
    }
}
