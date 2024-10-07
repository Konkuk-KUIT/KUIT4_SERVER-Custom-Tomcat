package Controller;

import db.MemoryUserRepository;
import http.HttpRequest;
import http.HttpResponse;
import model.User;

import java.io.IOException;

import static Constant.HttpHeaderType.*;
import static Constant.HttpMethodType.*;
import static Constant.Url.*;
import static Constant.UserQueryKey.*;

public class LoginController implements Controller {
    private final MemoryUserRepository userRepository = MemoryUserRepository.getInstance();
    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        if (httpRequest.getMethod().equals((POST.getMessage()))) {
            handleLoginPost(httpRequest, httpResponse);
        }
    }

    private void handleLoginPost(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        String userId = httpRequest.getBody(USERID.getValue());
        String password = httpRequest.getBody(PASSWORD.getValue());

        User user = userRepository.findUserById(userId);

        if (user != null && user.getPassword().equals(password)) {
            httpResponse.addHeader(COOKIE, "logined=true");
            httpResponse.redirect(INDEX_HTML.getMessage());
            return;
        }

        httpResponse.redirect(USER_LOGIN_FAILED_HTML.getMessage());
    }
}


