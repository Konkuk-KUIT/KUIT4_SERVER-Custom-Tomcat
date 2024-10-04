package controller;

import enums.Url;
import http.util.HttpRequest;
import http.util.HttpResponse;

public class HomeController implements Controller {
    @Override
    public void execute(HttpRequest request, HttpResponse response) throws Exception {
        response.forward("webapp" + Url.INDEX_HTML.getPath());
    }
}
