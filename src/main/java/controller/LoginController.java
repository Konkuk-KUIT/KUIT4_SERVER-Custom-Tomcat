package controller;

import db.Repository;
import http.HttpRequest;
import http.HttpResponse;
import model.User;

import static enums.http.HttpMethod.POST;
import static enums.key.UserQueryKey.PASSWORD;
import static enums.key.UserQueryKey.USERID;
import static enums.route.StaticRoute.*;

public class LoginController implements Controller{
    Repository repository;

    public LoginController(Repository repository) {
        this.repository = repository;
    }

    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) {
        boolean isLoginSuccessed = false;

        // if (httpRequest.getHttpMethod().equals("GET")){ } // 일단 POST 방식으로 전송되니까 보류

        if (httpRequest.getHttpMethod().equals(POST.getValue())) {
            String userId = httpRequest.getBody().get(USERID.getValue());
            String password = httpRequest.getBody().get(PASSWORD.getValue());

            User user = repository.findUserById(userId);
            if(user != null){
                if(user.getPassword().equals(password)){
                    isLoginSuccessed = true;
                }
            }
        }

        if(isLoginSuccessed){
            String location = INDEX_HTML.getRoute();
            httpResponse.redirectSettingCookie(location);
        }
        if(!isLoginSuccessed){
            String location = LOGIN_FAILED_HTML.getRoute();
            httpResponse.redirect(location);
        }
    }
}
