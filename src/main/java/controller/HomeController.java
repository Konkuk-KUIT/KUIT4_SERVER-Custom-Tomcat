package controller;

import http.request.HttpRequest;
import http.response.HttpResponse;

import java.io.IOException;

import static http.request.Url.INDEX_HTML;

public class HomeController implements Controller{
    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException{

        //요구사항 1: index.html 반환하기
        httpResponse.redirect(INDEX_HTML.getPath());
    }
}
