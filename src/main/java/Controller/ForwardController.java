package Controller;

import http.HttpRequest;
import http.HttpResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static Constant.StatusCode.*;
import static Constant.Url.*;

public class ForwardController implements Controller {

    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        if (Files.exists(Paths.get(WEBAPP + httpRequest.getPath()))) {
            httpResponse.forward(httpRequest.getPath());
            return;
        }

        httpResponse.setStatus(NOT_FOUND);
        //httpResponse.forward("404 페이지"); 404 페이지 없어서 주석처리
    }
}
