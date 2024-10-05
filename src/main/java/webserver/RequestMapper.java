package webserver;

import controller.*;
import http.request.HttpRequest;
import http.response.HttpResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static http.Url.*;

public class RequestMapper {

    private final Map<String, Controller> controllerMap = new HashMap<>();
    private final HttpRequest httpRequest;
    private final HttpResponse httpResponse;

    public RequestMapper(HttpResponse httpResponse, HttpRequest httpRequest) {
        this.httpResponse = httpResponse;
        this.httpRequest = httpRequest;

        initControllers();
    }

    // URL과 컨트롤러를 매핑하는 메서드
    private void initControllers() {
        controllerMap.put(ROOT.getUrl(), new HomeController());
        controllerMap.put(INDEX.getUrl(), new HomeController());
        controllerMap.put(USER_FORM.getUrl(), new ForwardController());
        controllerMap.put(USER_LOGIN_HTML.getUrl(), new ForwardController());
        controllerMap.put(USER_SIGNUP.getUrl(), new SignUpController());
        controllerMap.put(USER_LOGIN.getUrl(), new LoginController());
        controllerMap.put(USER_LIST_HTML.getUrl(), new ListController());
        controllerMap.put(USER_LIST.getUrl(), new ListController());
    }

    public void proceed() throws IOException {
        Controller controller = controllerMap.get(httpRequest.getPath());

        if(controller != null) {
            controller.execute(httpRequest, httpResponse);
            return;
        }
        httpResponse.forward(httpRequest.getPath());
    }
}
