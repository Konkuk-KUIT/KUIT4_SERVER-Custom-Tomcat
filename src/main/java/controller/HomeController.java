package controller;

import webserver.Request;
import webserver.Response;

import java.io.IOException;

public class HomeController implements Controller{
    @Override
    public void execute(Request request, Response response) throws IOException {
        response.redirect("/index.html");
    }
}
