package http.request;

import controller.*;
import http.constant.HttpURL;
import http.response.HttpResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static http.constant.HttpURL.*;

public class RequestMapper {
    private static final Map<String, Controller> requestMap = new HashMap<>();
    private final HttpRequest httpRequest;
    private final HttpResponse httpResponse;
    private final Controller controller;

    public RequestMapper(HttpResponse httpResponse, HttpRequest httpRequest) {
        this.httpResponse = httpResponse;
        this.httpRequest = httpRequest;
        controller = requestMap.get(httpRequest.getUrl());

        requestMap.put("/",new IndexController());
        requestMap.put(INDEX.getUrl(), new IndexController());
        requestMap.put(SIGNUP.getUrl(), new SignUpController());
        requestMap.put(LOGIN.getUrl(), new LoginController());
        requestMap.put(USER_LIST.getUrl(), new UserListController());

    }


    public void proceed() throws IOException {
        if (controller != null) {
            controller.execute(httpRequest, httpResponse);
            return;
        }
        httpResponse.forward(httpRequest.getUrl());
    }
}
