package mapper;

import controller.*;
import db.MemoryUserRepository;
import http.request.HttpRequest;
import http.response.HttpResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static constant.Url.*;

public class RequestMapper {

    private Map<String, Controller> controllers;
    private HttpRequest request;
    private HttpResponse response;

    public RequestMapper(HttpRequest request, HttpResponse response) {
        this.request = request;
        this.response = response;
        this.controllers = new HashMap<>();

        controllers.put(ROOT.getUrl(), new HomeController());
        controllers.put(USER_SIGNUP.getUrl(), new SignUpController(MemoryUserRepository.getInstance()));
        controllers.put(USER_LOGIN.getUrl(), new LoginController(MemoryUserRepository.getInstance()));
        controllers.put(USER_USERLIST.getUrl(), new ListController());
    }

    public void proceed() throws IOException {
        Controller controller = controllers.get(request.getUrl());
        Controller forwardController = new ForwardController();

        // .css 확장자로 들어오는 styles.css 파일을 인식해주기 위해 .css에 대한 코드도 추가
        if(request.getMethod().equals("GET") && (request.getUrl().endsWith(".html") || request.getUrl().endsWith(".css"))){
            forwardController.execute(request, response);
        } else{
            controller.execute(request, response);
        }


    }
}
