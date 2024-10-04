package webserver;

import db.MemoryUserRepository;
import http.request.HttpRequest;
import http.util.HttpRequestUtils;
import model.User;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static http.HttpHeader.*;
import static http.HttpStatusCode.*;
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
            byte[] body;

            // /index.html 요청 처리
            if (ROOT.getUrl().equals(url) || INDEX.getUrl().equals(url)) {
                body = Files.readAllBytes(Paths.get(WEBAPP.getUrl() + INDEX.getUrl()));
                response200Header(dos, body.length);
                responseBody(dos, body);
                return;
            }

            // /user/form.html 요청 처리
            if (USER_FORM.getUrl().equals(url)) {
                body = Files.readAllBytes(Paths.get(WEBAPP.getUrl() + USER_FORM.getUrl()));
                response200Header(dos, body.length);
                responseBody(dos, body);
                return;
            }

            // /user/signup 요청 처리
            if (url.startsWith(USER_SIGNUP.getUrl())) {
                handlePostSignUp(httpRequest, dos);
                return;
            }

            // /user/login.html 요청 처리
            if (USER_LOGIN_HTML.getUrl().equals(url)) {
                body = Files.readAllBytes(Paths.get(WEBAPP.getUrl() + USER_LOGIN_HTML.getUrl()));
                response200Header(dos, body.length);
                responseBody(dos, body);
                return;
            }

            // /user/login 요청 처리
            if (USER_LOGIN.getUrl().equals(url)) {
                handleLogin(httpRequest, dos);
                return;
            }

            // /user/login_failed.html 요청 처리
            if (USER_LOGIN_FAILED.getUrl().equals(url)) {
                body = Files.readAllBytes(Paths.get(WEBAPP.getUrl() + USER_LOGIN_FAILED.getUrl()));
                response200Header(dos, body.length);
                responseBody(dos, body);
                return;
            }

            // /user/userList, /user/list.html 요청 처리
            // home에서는 /user/userList, 그 외에는 /user/list.html
            if (USER_LIST.getUrl().equals(url) || USER_LIST_HTML.getUrl().equals(url)) {
                String cookie = httpRequest.getHeader(COOKIE.getValue());

                if (LOGINED_TRUE.getValue().equals(cookie)) {
                    body = Files.readAllBytes(Paths.get(WEBAPP.getUrl() + USER_LIST_HTML.getUrl()));
                    response200Header(dos, body.length);
                    responseBody(dos, body);
                    return;
                }

                // 로그인되지 않은 경우 로그인 페이지로 리다이렉트
                response302RedirectHeader(dos, USER_LOGIN_HTML.getUrl());
                return;
            }

            // css 파일 요청 처리
            if (url.endsWith(CSS_EXTENSION.getUrl())) {
                body = Files.readAllBytes(Paths.get(WEBAPP.getUrl() + url));
                response200HeaderCss(dos, body.length);
                responseBody(dos, body);
                return;
            }

            // 404 처리
            body = "404 Not Found".getBytes();
            response404Header(dos, body.length);
            responseBody(dos, body);

        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    // 로그인 처리
    private void handleLogin(HttpRequest httpRequest, DataOutputStream dos) throws IOException {
        String body = httpRequest.getBody();
        Map<String, String> params = HttpRequestUtils.parseQueryParameter(body);

        String userId = params.get(USER_ID.getKey());
        String password = params.get(PASSWORD.getKey());

        if (userId == null || password == null) {
            // 로그인이 잘못되었을 때 에러 처리
            response302RedirectWithCookie(dos, USER_LOGIN_FAILED.getUrl(), LOGINED_FALSE.getValue());
            return;
        }

        User user = memoryUserRepository.findUserById(userId);

        if (user != null && user.getPassword().equals(password)) {
            response302RedirectWithCookie(dos, INDEX.getUrl(), LOGINED_TRUE.getValue());
        } else {
            response302RedirectWithCookie(dos, USER_LOGIN_FAILED.getUrl(), LOGINED_FALSE.getValue());
        }
    }

    // POST 방식 회원가입 처리
    private void handlePostSignUp(HttpRequest httpRequest, DataOutputStream dos) throws IOException {
        String body = httpRequest.getBody();
        Map<String, String> params = HttpRequestUtils.parseQueryParameter(body);

        User user = new User(params.get(USER_ID.getKey()), params.get(PASSWORD.getKey()), params.get(NAME.getKey()), params.get(EMAIL.getKey()));

        memoryUserRepository.addUser(user);

        // 302 리다이렉트 응답
        response302RedirectHeader(dos, INDEX.getUrl());
    }

    // 쿠키 정보 추출
    private String getCookie(BufferedReader br) throws IOException {
        String cookie = null;

        while (true) {
            final String line = br.readLine();

            if (line.isEmpty()) {
                break;
            }

            if (line.startsWith(COOKIE.getValue())) {
                cookie = line.split(": ")[1];
            }
        }
        return cookie;
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 " + OK + " \r\n");
            dos.writeBytes(CONTENT_TYPE.getValue() + ": text/html;charset=utf-8\r\n");
            dos.writeBytes(CONTENT_LENGTH.getValue()+": " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response200HeaderCss(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 " + OK + " \r\n");
            dos.writeBytes(CONTENT_TYPE.getValue() + ": text/css;charset=utf-8\r\n");
            dos.writeBytes(CONTENT_LENGTH.getValue()+": " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response404Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 " + NOT_FOUND + " \r\n");
            dos.writeBytes(CONTENT_TYPE.getValue() + ": text/html;charset=utf-8\r\n");
            dos.writeBytes(CONTENT_LENGTH.getValue()+": " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    // 302 리다이렉트 응답 처리
    private void response302RedirectHeader(DataOutputStream dos, String location) {
        try {
            dos.writeBytes("HTTP/1.1 " + FOUND + " \r\n");
            dos.writeBytes(LOCATION.getValue()+": " + location + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    // 302 리다이렉트 응답 처리 (Cookie 포함)
    private void response302RedirectWithCookie(DataOutputStream dos, String location, String cookie) {
        try {
            dos.writeBytes("HTTP/1.1 " + FOUND + " \r\n");
            dos.writeBytes(LOCATION.getValue()+": " + location + "\r\n");
            dos.writeBytes(SET_COOKIE.getValue()+": " + cookie + "; Path=/\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

}