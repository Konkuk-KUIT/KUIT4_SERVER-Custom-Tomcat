package controller;

import constants.HttpHeader;
import constants.StatusCode;
import http.HttpRequest;
import http.HttpResponse;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ForwardController implements Controller {

    private static final String WEB_ROOT = "webapp";

    @Override
    public void execute(HttpRequest request, HttpResponse response) throws Exception {
        // 요청한 파일 경로
        String path = request.getPath();
        Path filePath = Paths.get(WEB_ROOT, path);

        if (Files.exists(filePath)) {
            // 파일이 존재하면 해당 파일 서빙
            byte[] body = Files.readAllBytes(filePath);
            response.setStatusCode(StatusCode.OK);
            response.setBody(body);
            response.addHeader(HttpHeader.CONTENT_TYPE, Files.probeContentType(filePath));
            response.forward();
        } else {
            // 파일이 존재하지 않으면 404 응답
            response.setStatusCode(StatusCode.NOT_FOUND);
            response.setBody("404 Not Found".getBytes());
            response.addHeader(HttpHeader.CONTENT_TYPE, "text/html");
            response.forward();
        }
    }
}