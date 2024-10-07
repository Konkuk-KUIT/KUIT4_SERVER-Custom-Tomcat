package webserver.Controller;

import db.MemoryUserRepository;
import model.User;
import webserver.HttpRequest;
import webserver.HttpResponse;
import java.io.IOException;
import java.util.Map;
import static webserver.enums.HttpUrl.*;

public class LoginController implements Controller {
    private final MemoryUserRepository userRepository = MemoryUserRepository.getInstance();

    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        //request에서 바디의 아이디와 비밀번호 파싱
        Map<String, String> params = request.parseQueryString();
        User user = userRepository.findUserById(params.get("userId"));
        
        //userRepository에 있는지 확인
        if (user != null && user.getPassword().equals(params.get("password"))) {
             response.redirect(HTTP_INDEX_HTML.getValue(), true);
             return;
        }
        response.redirect(HTTP_LOGIN_FAILD_HTML.getValue(), false);
    }
}
