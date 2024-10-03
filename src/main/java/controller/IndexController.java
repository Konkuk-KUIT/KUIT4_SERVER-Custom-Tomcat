package controller;

import http.constant.HttpURL;
import http.request.HttpRequest;
import http.response.HttpResponse;

import java.io.IOException;

import static http.constant.HttpURL.*;

public class IndexController implements Controller {

    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        response.forward(INDEX.getUrl());
    }
}
