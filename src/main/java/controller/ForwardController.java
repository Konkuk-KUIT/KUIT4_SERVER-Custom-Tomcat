package controller;

import http.request.HttpRequest;
import http.response.HttpResponse;

import java.io.IOException;

public class ForwardController implements Controller{
    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {

        String requestedUrl = httpRequest.getUrl(); // 요청된 파일

        httpResponse.forward(requestedUrl);

    }
}
