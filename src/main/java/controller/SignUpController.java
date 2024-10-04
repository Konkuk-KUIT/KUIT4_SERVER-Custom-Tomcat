package controller;

import db.MemoryUserRepository;
import http.request.HttpRequest;
import http.response.HttpResponse;
import http.util.HttpRequestUtils;
import model.User;

import java.io.IOException;
import java.util.Map;

import static http.Url.INDEX;
import static model.UserQueryKey.*;

public class SignUpController implements Controller {

    private final MemoryUserRepository memoryUserRepository = MemoryUserRepository.getInstance();

    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        String body = request.getBody();
        Map<String, String> params = HttpRequestUtils.parseQueryParameter(body);

        User user = new User(params.get(USER_ID.getKey()), params.get(PASSWORD.getKey()), params.get(NAME.getKey()), params.get(EMAIL.getKey()));

        memoryUserRepository.addUser(user);

        response.redirect(INDEX.getUrl(), null);
    }
}
