package webserver;

import Enum.Url;
import Controller.*;
import HttpRequest.HttpRequest;
import HttpResponse.HttpResponse;

import java.util.HashMap;
import java.util.Map;

import static Enum.Method.GET;
import static Enum.Url.*;

public class RequestMapper {

    private Map<String, Controller> controllerMap;
    private final HttpRequest httpRequest;
    private final HttpResponse httpResponse;

    public RequestMapper(HttpRequest httpRequest, HttpResponse httpResponse) {
        this.httpRequest = httpRequest;
        this.httpResponse = httpResponse;
        initControllerMap();
    }

    public void proceed() {
        String method = httpRequest.getMethod();
        String url = httpRequest.getUrl();

        Controller controller = controllerMap.get(url);
        if (controller != null) {
            controller.execute(httpRequest, httpResponse);
            return;
        }
        if (method.equals(GET.getMethod()) && url.endsWith(".html")) {
            controller = new ForwardController();
            controller.execute(httpRequest, httpResponse);
        }
    }

    private void initControllerMap() {
        controllerMap = new HashMap<>();
        controllerMap.put(ROOT.getUrl(), new HomeController());
        controllerMap.put(SIGNUP.getUrl(), new SignUpController());
        controllerMap.put(LOGIN.getUrl(), new LoginController());
        controllerMap.put(USER_LIST.getUrl(), new ListController());
        controllerMap.put(LIST.getUrl(), new ListController());
    }
}
