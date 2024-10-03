package controller;

import constant.HttpMethod;
import db.MemoryUserRepository;
import db.Repository;
import http.request.HttpRequest;
import http.response.HttpResponse;
import model.User;

import java.io.IOException;

import static constant.HttpMethod.*;
import static constant.QueryKey.*;
import static constant.QueryKey.EMAIL;
import static constant.Url.INDEX_HTML;

public class SignUpController implements Controller {

    Repository repository;

    public SignUpController(Repository repository) {
        this.repository = repository;
    }

    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {

        if (request.getMethod().equals(GET.getMethod())) {
            if (request.getUrl().contains("?")) {

                String userId = request.getQueryParamValue(USERID.getKey());
                String password = request.getQueryParamValue(PASSWORD.getKey());
                String name = request.getQueryParamValue(NAME.getKey());
                String email = request.getQueryParamValue(EMAIL.getKey());

                User user = new User(userId, password, name, email);

                repository.addUser(user);

                response.redirect(INDEX_HTML.getUrl());
            }
        }

        if (request.getMethod().equals(POST.getMethod())) {
            String userId = request.getBodyParamValue(USERID.getKey());
            String password = request.getBodyParamValue(PASSWORD.getKey());
            String name = request.getBodyParamValue(NAME.getKey());
            String email = request.getBodyParamValue(EMAIL.getKey());

            User user = new User(userId, password, name, email);

            repository.addUser(user);

            response.redirect(INDEX_HTML.getUrl());
        }
    }
}
