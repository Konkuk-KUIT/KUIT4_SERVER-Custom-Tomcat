package webserver;

import constant.URL;
import controller.*;
import http.request.HttpRequest;
import http.response.HttpResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RequestMapper {

    //url, controller가 key value 형태로 저장
    private static Map<String, Controller> controllers = new HashMap<>();
    private final HttpResponse response;
    private final HttpRequest request;

    public RequestMapper(HttpRequest request, HttpResponse response) {
        this.response = response;
        this.request = request;
    }

    static{
        controllers.put(URL.INDEX.getUrl(), new IndexController());
        controllers.put(URL.SIGNUP.getUrl(), new SignUpController());
        controllers.put(URL.LOGIN.getUrl(), new LoginController());
        controllers.put(URL.USER_LIST.getUrl(), new UserListController());
        controllers.put(URL.CSS.getUrl(), new CssController());
    }


    public void proceed() throws IOException {
        Controller controller = controllers.get(request.getUrl());
        if (controller != null) {
            controller.execute(request, response);
        }
        response.forward(request.getUrl());
    }


}
