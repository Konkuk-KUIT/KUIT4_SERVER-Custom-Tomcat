package controller;

import http.HttpRequest;
import http.HttpResponse;

import java.io.IOException;

public class CssController implements Controller {
    @Override
    public void execute(HttpRequest request, HttpResponse response) {
        String path = request.getPath();
        try {
            response.addHeader("Content-Type", "text/css");
            response.forward("/css/styles.css");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}