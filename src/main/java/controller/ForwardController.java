package controller;

import http.util.HttpRequest;
import http.util.HttpResponse;

public class ForwardController implements Controller {
    @Override
    public void execute(HttpRequest request, HttpResponse response) throws Exception {
        String path = request.getPath();
        response.forward("webapp" + path);
    }
}
