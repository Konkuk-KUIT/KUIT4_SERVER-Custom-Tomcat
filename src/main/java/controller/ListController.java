package controller;

import db.MemoryUserRepository;
import http.HttpRequest;
import http.HttpResponse;
import http.util.HttpRequestUtils;
import model.User;

import java.io.IOException;
import java.util.Map;

public class ListController implements Controller {
    @Override
    public void execute(HttpRequest request, HttpResponse response) {
        try {
            if (request.getHeaders().get("Cookie") != null && request.getHeaders().get("Cookie").contains("logined=true")) {
                response.forward("/user/list.html");
            } else {
                response.redirect("/user/login.html");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
