package controller;

import http.util.HttpRequest;
import http.util.HttpResponse;

public class ListController implements Controller {
    @Override
    public void execute(HttpRequest request, HttpResponse response) throws Exception {
        response.forward("/user/list.html");
    }
}
