package controller;

import db.MemoryUserRepository;
import db.Repository;
import http.HttpRequest;
import http.HttpResponse;
import model.User;

import java.util.Map;

import static enums.http.HttpMethod.GET;
import static enums.http.HttpMethod.POST;
import static enums.key.UserQueryKey.*;
import static enums.route.StaticRoute.*;

public class SignUpController implements Controller{
    Repository repository = MemoryUserRepository.getInstance();

    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) {

        if (httpRequest.getHttpMethod().equals(GET.getValue())){
            Map<String, String> queryMap = httpRequest.getQueryMap();
            User user = makeUserFromMap(queryMap);
            repository.addUser(user);
        }

        // todo : application/x-www-form-urlencoded로 전달되는 값 decode??? >> URLDecoder
        if (httpRequest.getHttpMethod().equals(POST.getValue())) {
            Map<String, String> bodyMap = httpRequest.getBodyMap();
            User user = makeUserFromMap(bodyMap);
            repository.addUser(user);
        }

        String location = INDEX_HTML.getRoute();
        httpResponse.redirect(location);
    }

    private User makeUserFromMap(Map<String, String> map){
        String userId = map.get(USERID.getValue());
        String password = map.get(PASSWORD.getValue());
        String name = map.get(NAME.getValue());
        String email = map.get(EMAIL.getValue());
        return User.of(userId, password, name, email);
    }
}
