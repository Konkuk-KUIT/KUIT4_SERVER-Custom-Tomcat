package webserver.controller;

import webserver.HttpRequest;
import webserver.HttpResponse;
import enums.URLPath;

import java.io.IOException;

public class HomeController implements Controller {
    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
            response.forward(URLPath.INDEX.getPath());
    }
}
