package controller;

import db.MemoryUserRepository;
import http.request.HttpRequest;
import http.response.HttpResponse;
import http.util.HttpRequestUtils;
import model.User;

import java.io.IOException;
import java.util.Map;

import static http.request.Url.INDEX_HTML;
import static model.UserQueryKey.*;

public class SignUpController implements Controller{

    MemoryUserRepository memoryUserRepository = MemoryUserRepository.getInstance();

    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {

        String queryString = httpRequest.getQueryString();
        Map<String, String> query;

        //요구사항 2 get 방식으로 회원가입
        // 쿼리 스트링을 파싱하여 Map으로 변환
        if (queryString != null) {
            query = HttpRequestUtils.parseQueryParameter(queryString);
        }
        //요구사항 3 POST 방식으로 회원가입
        else{
            query = httpRequest.getQueryParams();
        }

        User user = new User(query.get(USERID.getKey()),query.get(PASSWORD.getKey())
                ,query.get(NAME.getKey()),query.get(EMAIL.getKey()));
        memoryUserRepository.addUser(user);
        httpResponse.redirect(INDEX_HTML.getPath());
    }
}
