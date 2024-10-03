package Controller;

import HttpRequest.HttpRequest;
import HttpResponse.HttpResponse;

public interface Controller {

    public abstract void execute(HttpRequest httpRequest, HttpResponse httpResponse);

}
