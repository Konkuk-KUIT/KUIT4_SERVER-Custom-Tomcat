package controller;

import db.MemoryUserRepository;
import http.HttpRequest;
import http.HttpResponse;
import http.util.HttpRequestUtils;
import model.User;

import java.io.IOException;
import java.util.Map;

public class HomeController implements Controller {
    @Override
    public void execute(HttpRequest request, HttpResponse response) {
        try {
            response.forward("/index.html");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}