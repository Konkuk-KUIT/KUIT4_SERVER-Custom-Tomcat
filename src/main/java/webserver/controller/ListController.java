package webserver.controller;

import webserver.HttpRequest;
import webserver.HttpResponse;
import webserver.URLPath;

import java.io.IOException;

public class ListController implements Controller {
    @Override
    public void execute(HttpRequest request, HttpResponse response) {
        String cookieHeader = request.getHeaders().get("Cookie");
        // 요구사항 6 :
        try {
            if (cookieHeader == null || !(cookieHeader.contains("logined=true"))) {
                response.redirect(URLPath.INDEX.getPath());
            }
            response.forward(URLPath.LIST.getPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
