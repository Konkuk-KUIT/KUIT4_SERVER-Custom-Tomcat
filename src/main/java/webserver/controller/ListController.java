package webserver.controller;

import webserver.HttpRequest;
import webserver.HttpResponse;
import enums.URLPath;

import java.io.IOException;

public class ListController implements Controller {
    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        String cookieHeader = request.getHeader("Cookie");
        // 요구사항 6 :
        if (cookieHeader == null || !(cookieHeader.contains("logined=true"))) {
            response.redirect(URLPath.INDEX.getPath());
            return;
        }
        response.forward(URLPath.LIST.getPath());

    }
}
