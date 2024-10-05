package controller;

import webserver.Request;
import webserver.Response;
import java.io.IOException;
import java.util.Map;

public class UserListController implements Controller{
    @Override
    public void execute(Request request, Response response) throws IOException {
        Map<String, String> headers = request.getHeaders();
        String cookie = headers.get("Cookie");
        System.out.println(cookie);

        if (cookie != null && cookie.contains("logined=true")) {
            response.redirect("/user/list.html");
        } else {
            response.redirect("/user/login.html");
        }
    }
}
