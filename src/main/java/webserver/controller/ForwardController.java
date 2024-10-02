package webserver.controller;

import webserver.HttpRequest;
import webserver.HttpResponse;
import webserver.URLPath;

import java.io.IOException;

public class ForwardController implements Controller {
    @Override
    public void execute(HttpRequest request, HttpResponse response) {
        try {
            response.forward(URLPath.INDEX.getPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
