package webserver;

import db.MemoryUserRepository;
import db.Repository;
import http.request.HttpRequest;
import http.response.HttpResponse;
import model.User;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import static constant.HttpHeaderTitle.*;
import static constant.HttpMethod.*;
import static constant.QueryKey.*;
import static constant.StatusCode.*;
import static constant.Url.*;

public class RequestHandler implements Runnable {
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());
    Repository repository = MemoryUserRepository.getInstance();

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
            HttpResponse httpResponse = new HttpResponse(dos);

            // 요구사항 1
            //80 포트로 들어오거나, index.html로 주소가 들어올 경우 index.html을 출력하도록 함
            if (httpRequest.getUrl().equals(ROOT.getUrl()) || httpRequest.getUrl().equals(INDEX_HTML.getUrl())) {

                httpResponse.forward(INDEX_HTML.getUrl());
            }

            // 요구사항 2
            if (httpRequest.getMethod().equals(GET.getMethod()) && httpRequest.getUrl().equals(USER_FORM_HTML.getUrl())) {

                httpResponse.forward(USER_FORM_HTML.getUrl());
            }

            // 요구사항 2
            if (httpRequest.getMethod().equals(GET.getMethod()) && httpRequest.getUrl().contains(USER_SIGNUP.getUrl())) {

                if (httpRequest.getUrl().contains("?")) {

                    String userId = httpRequest.getQueryParamValue(USERID.getKey());
                    String password = httpRequest.getQueryParamValue(PASSWORD.getKey());
                    String name = httpRequest.getQueryParamValue(NAME.getKey());
                    String email = httpRequest.getQueryParamValue(EMAIL.getKey());

                    User user = new User(userId, password, name, email);

                    repository.addUser(user);

                    // 요구사항 4 적용
                    httpResponse.redirect(INDEX_HTML.getUrl());
                }
            }

            // 요구사항 3
            // POST 방식으로 전송하면 쿼리 파라미터가 사라진다. 즉 parseQueryParameter를 통해서 값을 얻어올 수 없다.
            // 대신 POST 방식에서는 이 쿼리 파라미터가 body 안에 들어간다.
            if (httpRequest.getMethod().equals(POST.getMethod()) && httpRequest.getUrl().equals(USER_SIGNUP.getUrl())) {

                String userId = httpRequest.getBodyParamValue(USERID.getKey());
                String password = httpRequest.getBodyParamValue(PASSWORD.getKey());
                String name = httpRequest.getBodyParamValue(NAME.getKey());
                String email = httpRequest.getBodyParamValue(EMAIL.getKey());

                User user = new User(userId, password, name, email);

                repository.addUser(user);

                // 요구사항 4 적용
                httpResponse.redirect(INDEX_HTML.getUrl());
            }

            // 요구사항 5
            if (httpRequest.getUrl().equals(USER_LOGIN_HTML.getUrl())) {

                httpResponse.forward(USER_LOGIN_HTML.getUrl());
            }

            // 요구사항 5
            if (httpRequest.getUrl().equals(USER_LOGIN.getUrl())) {

                String userId = httpRequest.getBodyParamValue(USERID.getKey());
                String password = httpRequest.getBodyParamValue(PASSWORD.getKey());

                User findUser = repository.findUserById(userId);

                // 입력한 ID에 대한 회원 자체가 존재하지 않는 경우
                if (findUser == null) {
                    log.log(Level.INFO, "로그인에 실패했습니다.");

                    httpResponse.putHeader(SET_COOKIE.getHeaderTitle(), "logined=false");
                    httpResponse.redirect(USER_LOGIN_FAILED_HTML.getUrl());
                } else { // 입력한 ID에 대한 회원은 존재하는 경우

                    String findUserPassword = findUser.getPassword();

                    if (findUserPassword.equals(password)) {  // repository에서 찾아온 회원의 정보와 로그인 시 넘긴 회원의 정보가 일치하는 경우
                        log.log(Level.INFO, "로그인에 성공했습니다.");

                        httpResponse.putHeader(SET_COOKIE.getHeaderTitle(), "logined=true");
                        httpResponse.redirect(INDEX_HTML.getUrl());
                    } else {  // repository에서 찾아온 회원의 정보와 로그인 시 넘긴 회원의 정보가 일치하지 않는 경우
                        log.log(Level.INFO, "로그인에 실패했습니다.");

                        httpResponse.putHeader(SET_COOKIE.getHeaderTitle(), "logined=false");
                        httpResponse.redirect(USER_LOGIN_FAILED_HTML.getUrl());
                    }
                }

            }

            // 요구사항 5
            if (httpRequest.getUrl().equals(USER_LOGIN_FAILED_HTML.getUrl())) {

                httpResponse.forward(USER_LOGIN_FAILED_HTML.getUrl());
            }

            // 요구사항 6
            if (httpRequest.getUrl().equals(USER_USERLIST.getUrl())) {

                boolean login = httpRequest.checkLogin();

                if (login) {
                    httpResponse.redirect(USER_LIST_HTML.getUrl());
                } else {
                    httpResponse.redirect(USER_LOGIN_HTML.getUrl());
                }
            }

            // 요구사항 6
            if (httpRequest.getUrl().equals(USER_LIST_HTML.getUrl())) {

                boolean login = httpRequest.checkLogin();

                if (login) {
                    httpResponse.forward(USER_LIST_HTML.getUrl());
                } else {
                    httpResponse.redirect(USER_LOGIN_HTML.getUrl());
                }
            }

            // 요구사항 7
            // .css 파일 요청 처리
            if (httpRequest.getUrl().endsWith(CSS_EXTENSION.getUrl())) {
                httpResponse.forward(httpRequest.getUrl());
            }


        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

}
