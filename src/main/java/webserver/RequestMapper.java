package webserver;

import controller.*;
import enums.route.PageRoute;
import http.HttpRequest;
import http.HttpResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static enums.exception.ExceptionMessage.INVALID_REQUEST_URL;
import static enums.extension.FileExtension.CSS;
import static enums.extension.FileExtension.HTML;
import static enums.http.HttpMethod.GET;

public class RequestMapper {
    Controller controller;
    HttpRequest httpRequest;
    HttpResponse httpResponse;

    private RequestMapper(Controller controller, HttpRequest httpRequest, HttpResponse httpResponse) {
        this.controller = controller;
        this.httpRequest = httpRequest;
        this.httpResponse = httpResponse;
    }

    public static RequestMapper from(HttpRequest httpRequest, HttpResponse httpResponse){
        String requestMethod = httpRequest.getHttpMethod();
        String requestUrl = httpRequest.getUrl();

        if (requestMethod.equals(GET.getValue()) && requestUrl.endsWith(HTML.addFrontPoint()) || requestUrl.endsWith(CSS.addFrontPoint())) {
            Controller controller = new ForwardController();
            return new RequestMapper(controller, httpRequest, httpResponse);
        }

        Map <String,Controller> controllers = setControllerMap();
        Controller controller = controllers.get(requestUrl);

        if(controller == null) throw new IllegalArgumentException(INVALID_REQUEST_URL.getMessage());
        return new RequestMapper(controller, httpRequest, httpResponse);
    }

    private static Map<String, Controller> setControllerMap(){
        Map <String,Controller> controllers = new HashMap<String, Controller>();

        controllers.put(PageRoute.HOME.getRoute(), new HomeController());
        controllers.put(PageRoute.SIGNUP.getRoute(), new SignUpController());
        controllers.put(PageRoute.LOGIN.getRoute(), new LoginController());
        controllers.put(PageRoute.USER_LIST.getRoute(), new ListController());

        return controllers;
    }

    public void proceed() throws IOException {
        controller.execute(httpRequest, httpResponse);
    }
}
