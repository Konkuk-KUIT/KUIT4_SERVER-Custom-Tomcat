package Controller;

import HttpRequest.HttpRequest;
import HttpResponse.HttpResponse;
import db.MemoryUserRepository;
import db.Repository;
import http.util.HttpRequestUtils;
import model.User;

import java.util.Map;

import static Enum.Url.HOME;
import static Enum.UserQueryKey.*;

public class SignUpController implements Controller{

    public SignUpController() {
    }

    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) {
        //post 방식으로 회원가입하기
        String queryString = httpRequest.getBody();
        Map<String, String> queryMap = HttpRequestUtils.parseQueryParameter(queryString);

        httpResponse.redirect(HOME.getUrl());

        Repository repository = MemoryUserRepository.getInstance();
        repository.addUser(new User(queryMap.get(USER_ID.getUserQueryKey()),
                queryMap.get(PASSWORD.getUserQueryKey()),
                queryMap.get(NAME.getUserQueryKey()),
                queryMap.get(EMAIL.getUserQueryKey())));

//        //get 방식으로 회원가입하기
//        if (url.equals(SIGNUP_FORM.getUrl())) {
//            httpResponse.forward(SIGNUP_FORM_HTML.getUrl());
//        }
//
//        if (url.startsWith(SIGNUP.getUrl()) && method.equals(GET.getMethod())) {
//            String queryString = url.split("\\?")[1];
//            Map<String, String> queryMap = HttpRequestUtils.parseQueryParameter(queryString);
//
//            httpResponse.redirect(HOME.getUrl());
//
//            repository.addUser(new User(queryMap.get(USER_ID.getUserQueryKey()),
//                    queryMap.get(PASSWORD.getUserQueryKey()),
//                    queryMap.get(NAME.getUserQueryKey()),
//                    queryMap.get(EMAIL.getUserQueryKey())));
//        }
    }
}
