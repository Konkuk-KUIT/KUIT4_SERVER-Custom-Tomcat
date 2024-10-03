package Controller;

import HttpRequest.HttpRequest;
import HttpResponse.HttpResponse;
import static Enum.Url.HOME_HTML;

public class HomeController implements Controller{

    public HomeController() {

    }

    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) {
        httpResponse.forward(HOME_HTML.getUrl());
    }
}
