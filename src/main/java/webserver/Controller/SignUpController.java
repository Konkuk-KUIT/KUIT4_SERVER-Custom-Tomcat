package webserver.Controller;

import db.MemoryUserRepository;
import model.User;
import webserver.HttpRequest;
import webserver.HttpResponse;
import java.io.IOException;
import java.util.Map;
import static webserver.enums.HttpMethod.HTTP_POST;
import static webserver.enums.HttpUrl.HTTP_INDEX_HTML;

public class SignUpController implements Controller {
    private final MemoryUserRepository userRepository = MemoryUserRepository.getInstance();

    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        //Post 버전 회원가입
        if (request.getMethod().equals(HTTP_POST.getValue())) {
            handlePostSignup(request, response);
            return;
        }
        //GET 버전 회원가입
        handleGetSignup(request, response);
    }


    private void handleGetSignup(HttpRequest request, HttpResponse response) throws IOException {
        Map<String, String> params = request.parseQueryString();

        User user = new User(params.get("userId"), params.get("password"), params.get("name"), params.get("email"));
        userRepository.addUser(user);
        response.redirect(HTTP_INDEX_HTML.getValue(), false);
    }

    private void handlePostSignup(HttpRequest request, HttpResponse response) throws IOException {
        Map<String, String> params = request.parseQueryString();

        User user = new User(params.get("userId"), params.get("password"), params.get("name"), params.get("email"));
        userRepository.addUser(user);
        response.redirect(HTTP_INDEX_HTML.getValue(), false);
    }
}
