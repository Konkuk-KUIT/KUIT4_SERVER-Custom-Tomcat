package controller;

import constants.HttpHeader;
import constants.StatusCode;
import constants.Url;
import http.HttpRequest;
import http.HttpResponse;
import db.MemoryUserRepository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static http.util.HttpRequestUtils.parseCookies;

public class ListController implements Controller {

    private final MemoryUserRepository userRepository = MemoryUserRepository.getInstance();

    @Override
    public void execute(HttpRequest request, HttpResponse response) throws Exception {

        // 쿠키에서 로그인 여부 확인
        // 헤더에서 쿠키 가져오기
        String cookieHeader = request.getHeaders().get(HttpHeader.COOKIE);
        System.out.println("Cookie Header: " + cookieHeader); // 쿠키 확인
        boolean isLoggedIn = false;

        // 쿠키가 존재하는 경우 쿠키 값을 파싱하여 로그인 여부 확인
        if (cookieHeader != null) {
            Map<String, String> cookies = parseCookies(cookieHeader);
            isLoggedIn = "true".equals(cookies.get("logined")); // logined 쿠키 확인
        }

        if (isLoggedIn) {
            // 로그인된 사용자라면 list.html 파일 제공
            Path filePath = Paths.get("webapp" + Url.USER_LIST.getPath());
            if (Files.exists(filePath)) {
                byte[] body = Files.readAllBytes(filePath); // 파일 내용을 바이트 배열로 읽음
                response.setStatusCode(StatusCode.OK);
                response.setBody(body);
                response.addHeader(HttpHeader.CONTENT_TYPE, "text/html");
                response.forward(); // 응답 전송
            } else {
                // 파일이 존재하지 않는 경우 404 처리
                response.setStatusCode(StatusCode.NOT_FOUND);
                response.setBody("404 Not Found".getBytes());
                response.addHeader(HttpHeader.CONTENT_TYPE, "text/html");
                response.forward();
            }
        } else {
            // 로그인되지 않은 사용자라면 로그인 페이지로 리디렉트
            System.out.println("Redirecting to login page");
            response.redirect(Url.LOGIN.getPath());
        }
    }
}