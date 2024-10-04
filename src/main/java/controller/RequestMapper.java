package controller;

import java.util.HashMap;
import java.util.Map;

public class RequestMapper {

    // URL과 Controller 매핑을 위한 Map
    private final Map<String, Controller> controllers = new HashMap<>();

    // 생성자에서 URL과 Controller 매핑
    public RequestMapper() {
        controllers.put("/", new HomeController());
        controllers.put("/user/signup", new SignUpController());
        controllers.put("/user/login", new LoginController());
        controllers.put("/user/userList", new ListController());
        controllers.put("/user/login_failed.html", new ForwardController());  // Forward Controller 예시 추가
        controllers.put("/index.html", new ForwardController());  // Forward Controller 예시 추가
    }

    // URL에 해당하는 Controller를 반환
    public Controller getController(String url) {
        return controllers.get(url);
    }
}