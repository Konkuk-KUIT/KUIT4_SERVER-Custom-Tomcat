package controller;

import enums.http.header.RequestHeader;
import http.HttpRequest;
import http.HttpResponse;

import static enums.route.StaticRoute.*;

public class ListController implements Controller {
    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) {

        String cookie = httpRequest.getHeader().get(RequestHeader.COOKIE.getValue());

        if (cookie != null && cookie.contains("logined=true")) {
            String location = USER_LIST_HTML.getRoute();
            httpResponse.redirect(location);
        }
        else{
            String location = LOGIN_HTML.getRoute();
            httpResponse.redirect(location);
        }
    }
}
