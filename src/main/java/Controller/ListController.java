package Controller;

import HttpRequest.HttpRequest;
import HttpResponse.HttpResponse;

import static Enum.Url.LIST_HTML;
import static Enum.Url.LOGIN_FORM;

public class ListController implements Controller{
    public ListController() {
    }

    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) {
        boolean logined = httpRequest.isLogined();
        if (logined) {
            httpResponse.forward(LIST_HTML.getUrl());
        }
        if (!logined) {
            httpResponse.redirect(LOGIN_FORM.getUrl());
        }
    }
}
