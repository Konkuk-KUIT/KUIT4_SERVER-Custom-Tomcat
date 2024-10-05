package controller;

import constants.HttpHeader;
import constants.StatusCode;
import http.HttpRequest;
import http.HttpResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HomeController implements Controller {

    private static final String WEB_ROOT = "webapp";  // 정적 파일이 있는 루트 디렉토리

    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        // index.html 파일 경로 설정
        Path filePath = Paths.get(WEB_ROOT, "index.html");

        if (Files.exists(filePath)) {
            // index.html 파일이 존재할 경우 해당 파일 서빙
            byte[] body = Files.readAllBytes(filePath);
            response.setStatusCode(StatusCode.OK);
            response.setBody(body);
            response.addHeader(HttpHeader.CONTENT_TYPE, Files.probeContentType(filePath));  // 파일의 Content-Type 설정
            response.forward();
        } else {
            // index.html 파일이 없을 경우 404 응답 반환
            response.setStatusCode(StatusCode.NOT_FOUND);
            response.setBody("404 Not Found".getBytes());
            response.addHeader(HttpHeader.CONTENT_TYPE, "text/html");
            response.forward();
        }
    }
}