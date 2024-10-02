package webserver;

import controller.*;
import db.MemoryUserRepository;
import http.request.HttpRequest;
import http.request.RequestURL;
import http.response.HttpResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RequestMapper {
    private final HttpRequest httpRequest;
    private final HttpResponse httpResponse;

    private static final Map<String, Controller> controllers = new HashMap<>();
    private final Controller controller;

    public RequestMapper(HttpRequest httpRequest, HttpResponse httpResponse) {
        this.httpRequest = httpRequest;
        this.httpResponse = httpResponse;
        controller = controllers.get(httpRequest.getUrl());
    }
    static {
        controllers.put(RequestURL.SIGNUP.getUrl(),new SignUpController(MemoryUserRepository.getInstance()));
        controllers.put(RequestURL.LOGIN_POST.getUrl(), new LoginController(MemoryUserRepository.getInstance()));
        controllers.put(RequestURL.USER_LIST.getUrl(), new ListController());
        controllers.put(RequestURL.INDEX.getUrl(), new HomeController());
        controllers.put("/",new HomeController());
    }

    public void proceed() throws IOException {
        if (controller != null) {
            controller.execute(httpRequest, httpResponse);
            return;
        }
        httpResponse.forward(httpRequest.getUrl());
    }

}
