package webserver;

import controller.*;
import http.HttpRequest;
import http.HttpResponse;

import java.util.HashMap;
import java.util.Map;

public class RequestMapper {
    private final HttpRequest request;
    private final HttpResponse response;
    private static final Map<String, Controller> controllerMap;

    public RequestMapper(HttpRequest request, HttpResponse response) {
        this.request = request;
        this.response = response;
    }

    static{
        controllerMap = new HashMap<>();
        controllerMap.put("/", new HomeController());
        controllerMap.put("/user/signup", new SignUpController());
        controllerMap.put("/user/login", new LoginController());
        controllerMap.put("/user/userList", new ListController());
    }

    public void proceed() {
        Controller controller = getController();
        controller.execute(request, response);
    }

    private Controller getController() {
        String path = request.getPath();

        if (request.getMethod().equals("GET")) {
            if (path.endsWith(".css")) {
                return new CssController();
            } else if (path.endsWith(".html")) {
                return new ForwardController();
            }
        }
        return controllerMap.getOrDefault(path, new ForwardController());
    }
}