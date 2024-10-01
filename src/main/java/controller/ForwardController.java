package controller;

import java.io.IOException;
import http.HttpRequest;
import http.HttpResponse;

// url이 html로 들어오면 200으로 body에 html 파일 보내주기
public class ForwardController implements Controller{

    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        String path = httpRequest.getUrl();
        httpResponse.forward(path);
    }
}
