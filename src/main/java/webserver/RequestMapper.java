package webserver;

import webserver.Controller.*;

import java.util.HashMap;
import java.util.Map;
import static webserver.enums.HttpUrl.*;

public class RequestMapper {
    private static final Map<String, Controller> controllers = new HashMap<>();

    static {
        controllers.put(HTTP_ROOT.getValue(), new HomeController());
        controllers.put(HTTP_USER_LIST.getValue(), new ListController());
        controllers.put(HTTP_LIST_HTML.getValue(), new ListController());
        controllers.put(HTTP_USER_SIGNUP.getValue(), new SignUpController());
        controllers.put(HTTP_LOGIN.getValue(), new LoginController());
    }

    static Controller getController(HttpRequest request) {
        String path = request.getPath();

        // 먼저 정확한 매칭을 확인
        if (controllers.containsKey(path)) {
            return controllers.get(path);
        }

        // 마지막으로 endsWith 확인
        if (path.endsWith(HTTP_END_HTML.getValue())) {
            return new ForwardController();
        }
        if (path.endsWith(HTTP_END_CSS.getValue())) {
            return new CssController();
        }

        // 기본값 반환
        return new ForwardController();
    }
}