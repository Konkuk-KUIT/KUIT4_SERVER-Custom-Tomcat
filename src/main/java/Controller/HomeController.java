package Controller;

import http.HttpRequest;
import http.HttpResponse;

import java.io.IOException;

import static Constant.Url.*;

public class HomeController implements Controller{
    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        httpResponse.forward(INDEX_HTML.getMessage());
    }
}
