package Controller;

import HttpRequest.HttpRequest;
import HttpResponse.HttpResponse;
import db.MemoryUserRepository;
import db.Repository;
import http.util.HttpRequestUtils;
import model.User;

import java.util.Map;

import static Enum.Url.HOME;
import static Enum.Url.LOGIN_FAILED;
import static Enum.UserQueryKey.PASSWORD;
import static Enum.UserQueryKey.USER_ID;

public class LoginController implements Controller{

    public LoginController() {
    }
    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) {
        String queryString = httpRequest.getBody();
        Map<String, String> queryMap = HttpRequestUtils.parseQueryParameter(queryString);

        String inputUserId = queryMap.get(USER_ID.getUserQueryKey());
        String inputPassword = queryMap.get(PASSWORD.getUserQueryKey());

        Repository repository = MemoryUserRepository.getInstance();
        User user = repository.findUserById(inputUserId);
        if (user != null) {
            if (inputPassword.equals(user.getPassword())) {
                httpResponse.redirect(HOME.getUrl(), "logined=true");
                return;
            }
        }
        httpResponse.redirect(LOGIN_FAILED.getUrl());
    }
}
