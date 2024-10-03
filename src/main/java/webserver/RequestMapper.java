package webserver;

import controller.*;
import http.request.HttpRequest;
import http.response.HttpResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static http.request.HttpMethod.GET;
import static http.request.Url.*;

public class RequestMapper {
    private final HttpRequest httpRequest;
    private final HttpResponse httpResponse;
    private final Map<String, Controller> controllers;

    public RequestMapper(HttpRequest httpRequest, HttpResponse httpResponse) {
        this.httpRequest = httpRequest;
        this.httpResponse = httpResponse;
        this.controllers = new HashMap<>();
        MappingControllers();
    }

    private void MappingControllers() {

        // URL과 컨트롤러를 매핑
        controllers.put(ROOT.getPath(), new HomeController());
        controllers.put(USER_SIGNUP.getPath(), new SignUpController());
        controllers.put(USER_LOGIN.getPath(), new LoginController());
        controllers.put(USER_LIST.getPath(), new ListController());
    }

    public void proceed() throws IOException {

        String requestedUrl = httpRequest.getUrl();
        Controller controller = null;

        if (httpRequest.getMethod().isEqual(GET) &&
                (httpRequest.getUrl().endsWith(".html") || httpRequest.getUrl().endsWith(".css"))) {
            controller = new ForwardController();
        }

        if (controller == null) {
            controllers.get(requestedUrl).execute(httpRequest, httpResponse);
        }
        else {
            controller.execute(httpRequest, httpResponse);
        }
    }
}
