package Controller;

import http.HttpRequest;
import http.HttpResponse;

import java.io.IOException;

public interface Controller {
    void execute(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException;
}
