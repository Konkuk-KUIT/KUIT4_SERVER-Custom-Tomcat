package controller;

import enums.route.StaticRoute;
import http.HttpRequest;
import http.HttpResponse;

// url이 "/"로 들어오면 302로 /index.html로 redirect, 페이지 반환은 재요청 받으면 200이 반환
public class HomeController implements Controller{

    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) {
        String location = StaticRoute.INDEX_HTML.getRoute();
        httpResponse.redirect(location);
    }
}
