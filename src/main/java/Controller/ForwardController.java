package Controller;

import HttpRequest.HttpRequest;
import HttpResponse.HttpResponse;

public class ForwardController implements Controller{
    public ForwardController() {
    }

    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) {
        System.out.println("ForwardController");
        System.out.println("webapp" + httpRequest.getUrl());
        httpResponse.forward("webapp" + httpRequest.getUrl());
    }
}
