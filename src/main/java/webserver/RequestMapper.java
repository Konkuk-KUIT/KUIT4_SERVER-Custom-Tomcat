package webserver;

import http.HttpRequest;
import http.HttpResponse;
import Controller.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static Constant.HttpMethodType.*;
import static Constant.Url.*;

public class RequestMapper {
    private final HttpRequest httpRequest;
    private final HttpResponse httpResponse;
    private final Map<String, Controller> controllers;      //싱글톤 패턴 사용해서 관리하는거 추천

    public RequestMapper(HttpRequest httpRequest, HttpResponse httpResponse){
        this.httpRequest = httpRequest;
        this.httpResponse = httpResponse;
        this.controllers = new HashMap<>();

        initialize();
    }

    private void initialize(){
        controllers.put(ROOT.getMessage(), new HomeController());           //요구사항 1
        controllers.put(USER_SIGNUP.getMessage(), new SignUpController());  //요구사항 2,3,4
        controllers.put(USER_LOGIN.getMessage(), new LoginController());    //요구사항 5
        controllers.put(USER_USERLIST.getMessage(), new ListController());  //요구사항 6
    }

    public void proceed() throws IOException {
        String path = getPathWithoutQuery(httpRequest.getPath());
        Controller controller = controllers.get(path);

        if(httpRequest.getMethod().equals(GET.getMessage()) && httpRequest.getPath().endsWith(".html")){        //요구사항 1
            controller = new ForwardController();
        }

        controller.execute(httpRequest, httpResponse);
    }

    private String getPathWithoutQuery(String path){                        //get 방식 회원가입 커버하기 위함
        if(path.contains("?")){
            return path.split("\\?")[0];
        }
        return path;
    }
}


