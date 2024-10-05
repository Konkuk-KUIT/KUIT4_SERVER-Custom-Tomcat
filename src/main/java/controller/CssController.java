package controller;

import http.request.HttpRequest;
import http.response.HttpResponse;

import java.io.IOException;

public class CssController implements Controller {
    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        response.setCss(request.getUrl());
    }
}
