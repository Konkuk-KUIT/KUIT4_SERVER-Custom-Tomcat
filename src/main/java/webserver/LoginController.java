package webserver;

import db.MemoryUserRepository;
import http.util.HttpRequestUtils;
import model.User;

import java.io.IOException;
import java.util.Map;

public class LoginController implements Controller{
    @Override
    public void execute(HttpResponse httpResponse, HttpRequest2 httpRequest) throws IOException {
        Map<String, String> queryParms = HttpRequestUtils.parseQueryParameter(httpRequest.getBody());

        String userId = queryParms.get(UserQueryKey.USER_ID.getKey());
        String password = queryParms.get(UserQueryKey.PASSWORD.getKey());

        User user = MemoryUserRepository.getInstance().findUserById(userId);

        // 로그인 성공 확인
        if (user != null && user.getPassword().equals(password)) {
            httpResponse.redirect("/"); // 쿠키와 함께 리다이렉트
        } else {
            httpResponse.redirect("/user/login_failed.html");
        }

    }
}
