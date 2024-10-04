package webserver;

import db.MemoryUserRepository;
import http.request.HttpRequest;
import http.response.HttpResponse;
import http.util.HttpRequestUtils;
import model.User;

import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static http.HttpHeader.*;
import static http.Url.*;
import static model.UserQueryKey.*;

public class RequestHandler implements Runnable {
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    private final MemoryUserRepository memoryUserRepository = MemoryUserRepository.getInstance();

    public RequestHandler(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            HttpRequest httpRequest = HttpRequest.from(br);
            String url = httpRequest.getPath();

            HttpResponse httpResponse = new HttpResponse(out);

            // /index.html 요청 처리
            if (ROOT.getUrl().equals(url) || INDEX.getUrl().equals(url)) {
                httpResponse.forward( INDEX.getUrl());
                return;
            }

            // /user/form.html 요청 처리
            if (USER_FORM.getUrl().equals(url)) {
                httpResponse.forward(USER_FORM.getUrl());
                return;
            }

            // /user/signup 요청 처리
            if (url.startsWith(USER_SIGNUP.getUrl())) {
                handlePostSignUp(httpRequest, httpResponse);
                return;
            }

            // /user/login.html 요청 처리
            if (USER_LOGIN_HTML.getUrl().equals(url)) {
                httpResponse.forward(USER_LOGIN_HTML.getUrl());
                return;
            }

            // /user/login 요청 처리
            if (USER_LOGIN.getUrl().equals(url)) {
                handleLogin(httpRequest, httpResponse);
                return;
            }

            // /user/login_failed.html 요청 처리
            if (USER_LOGIN_FAILED.getUrl().equals(url)) {
                httpResponse.forward(USER_LOGIN_FAILED.getUrl());
                return;
            }

            // /user/userList, /user/list.html 요청 처리
            // home에서는 /user/userList, 그 외에는 /user/list.html
            if (USER_LIST.getUrl().equals(url) || USER_LIST_HTML.getUrl().equals(url)) {
                String cookie = httpRequest.getHeader(COOKIE.getValue());

                if (LOGINED_TRUE.getValue().equals(cookie)) {
                    httpResponse.forward(USER_LIST_HTML.getUrl());
                    return;
                }

                // 로그인되지 않은 경우 로그인 페이지로 리다이렉트
                httpResponse.redirect(USER_LOGIN_HTML.getUrl(), null);
                return;
            }

            // css 파일 요청 처리
            if (url.endsWith(CSS_EXTENSION.getUrl())) {
                httpResponse.forward(url);
                return;
            }

        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    // 로그인 처리
    private void handleLogin(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        String body = httpRequest.getBody();
        Map<String, String> params = HttpRequestUtils.parseQueryParameter(body);

        String userId = params.get(USER_ID.getKey());
        String password = params.get(PASSWORD.getKey());

        if (userId == null || password == null) {
            // 로그인이 잘못되었을 때 에러 처리
            httpResponse.redirect(USER_LOGIN_FAILED.getUrl(), LOGINED_FALSE.getValue());
            return;
        }

        User user = memoryUserRepository.findUserById(userId);

        if (user != null && user.getPassword().equals(password)) {
            httpResponse.redirect(INDEX.getUrl(), LOGINED_TRUE.getValue());
        } else {
            httpResponse.redirect(USER_LOGIN_FAILED.getUrl(), LOGINED_FALSE.getValue());
        }
    }

    // POST 방식 회원가입 처리
    private void handlePostSignUp(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        String body = httpRequest.getBody();
        Map<String, String> params = HttpRequestUtils.parseQueryParameter(body);

        User user = new User(params.get(USER_ID.getKey()), params.get(PASSWORD.getKey()), params.get(NAME.getKey()), params.get(EMAIL.getKey()));

        memoryUserRepository.addUser(user);

        // 302 리다이렉트 응답
        httpResponse.redirect(INDEX.getUrl(), null);
    }

}