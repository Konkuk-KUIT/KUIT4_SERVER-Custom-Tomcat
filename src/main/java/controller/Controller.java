package controller;

import java.io.IOException;
import http.HttpRequest;
import http.HttpResponse;

public interface Controller {
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException;
}
