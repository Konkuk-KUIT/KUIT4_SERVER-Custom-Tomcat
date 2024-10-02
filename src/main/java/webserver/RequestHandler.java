package webserver;

import db.Repository;
import http.constants.HttpHeader;
import http.request.HttpRequest;
import http.response.HttpResponse;
import http.util.IOUtils;
import model.User;
import model.constants.UserQueryKey;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static http.request.RequestURL.*;
import static http.util.HttpRequestUtils.parseQueryParameter;

public class RequestHandler implements Runnable{
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    private final Repository repository;

    public RequestHandler(Socket connection, Repository repository) {
        this.connection = connection;
        this.repository = repository;
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()){
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            HttpRequest httpRequest = HttpRequest.from(br);
            log.log(Level.INFO, "HTTP request url : " + httpRequest.getUrl());
            HttpResponse httpResponse = new HttpResponse(dos);

            byte[] body = new byte[0];

            // 요구 사항 1번
            if (httpRequest.getMethod().isEqual("GET") && httpRequest.getUrl().endsWith(".html")) {
                httpResponse.forward(httpRequest.getUrl());
                return;
            }

            if (httpRequest.getUrl().equals("/")) {
                httpResponse.forward(INDEX.getUrl());
                return;
            }

            // 요구 사항 2, 3, 4번
            if (httpRequest.getUrl().equals("/user/signup") && httpRequest.getMethod().isEqual("POST")) {
                Map<String, String> queryParameter = httpRequest.getQueryParametersFromBody();
                User user = new User(
                        queryParameter.get(UserQueryKey.ID.getKey()),
                        queryParameter.get(UserQueryKey.PASSWORD.getKey()),
                        queryParameter.get(UserQueryKey.NAME.getKey()),
                        queryParameter.get(UserQueryKey.EMAIL.getKey()));
                repository.addUser(user);
                httpResponse.redirect(INDEX.getUrl());
                return;
            }

            // 요구 사항 5번
            if (httpRequest.getUrl().equals("/user/login")) {
                Map<String, String> queryParameter = httpRequest.getQueryParametersFromBody();
                User user = repository.findUserById(queryParameter.get("userId"));
                login(httpResponse, queryParameter, user);
                return;
            }

            // 요구 사항 6번
            if (httpRequest.getUrl().equals("/user/userList")) {
                if (!httpRequest.getHeader(HttpHeader.COOKIE).equals("logined=true")) {
                    httpResponse.redirect(LOGIN.getUrl());
                    return;
                }
                httpResponse.forward(USER_LIST_HTML.getUrl());
            }

            // 요구 사항 7번
            if (httpRequest.getMethod().isEqual("GET") && httpRequest.getUrl().endsWith(".css")) {
                body = Files.readAllBytes(Paths.get(ROOT.getUrl() + httpRequest.getUrl()));
                response200HeaderWithCss(dos, body.length);
                responseBody(dos, body);
                return;
            }

            // image
            if (httpRequest.getMethod().isEqual("GET") && httpRequest.getUrl().endsWith(".jpeg")) {
                body = Files.readAllBytes(Paths.get(ROOT.getUrl() + httpRequest.getUrl()));
                response200Header(dos, body.length);
                responseBody(dos, body);
                return;
            }

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

    private void login(HttpResponse response, Map<String, String> queryParameter, User user) throws IOException {
        if (user != null && user.getPassword().equals(queryParameter.get("password"))) {
            response.put(HttpHeader.SET_COOKIE,"logined=true");
            response.redirect(INDEX.getUrl());
            return;
        }
        response.redirect(LOGIN_FAILED.getUrl());
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response200HeaderWithCss(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/css;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
            dos.flush();
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
