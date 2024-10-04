package Controller;

import db.MemoryUserRepository;
import db.Repository;
import http.util.HttpRequest;
import http.util.HttpRequestUtils;
import http.util.HttpResponse;
import model.User;

import java.util.Map;

import static enumClass.Url.INDEX_PAGE;
import static enumClass.Url.USER_LOGIN_FAILED_PAGE;
import static enumClass.UserQueryKey.*;

public class LoginController implements Controller {
    private final Repository repository = MemoryUserRepository.getInstance();

    @Override
    public void execute(HttpRequest request, HttpResponse response) throws Exception {
        Map<String, String> params = request.getBody() != null ? HttpRequestUtils.parseQueryParameter(request.getBody()) : null;
        if (params != null) {
            User user = repository.findUserById(params.get(USERID.getValue()));
            if (user != null && user.getPassword().equals(params.get(PASSWORD.getValue()))) {
                response.redirect(INDEX_PAGE.getValue());
            } else {
                response.redirect(USER_LOGIN_FAILED_PAGE.getValue());
            }
        }
    }
}
