package webserver;

import controller.*;
import db.MemoryUserRepository;
import http.HttpMethod;
import http.Url;
import http.request.HttpRequest;
import http.response.HttpResponse;
import http.util.HttpRequestUtils;
import model.User;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static http.HttpHeader.*;
import static http.HttpMethod.*;
import static http.Url.*;
import static model.UserQueryKey.*;

public class RequestHandler implements Runnable {
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    private Controller controller;

    public RequestHandler(Socket connection) {
        this.connection = connection;
        MemoryUserRepository repository = MemoryUserRepository.getInstance();
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            // HttpRequest, HttpResponse 생성
            HttpRequest httpRequest = HttpRequest.from(br);
            HttpResponse httpResponse = new HttpResponse(dos);

            // 요구 사항 1번
            if (httpRequest.getMethod().equals(GET.getMethod()) && httpRequest.getPath().endsWith(HTML_EXTENSION.getUrl())) {
                controller = new ForwardController();
            }

            if (httpRequest.getPath().equals(ROOT.getUrl())) {
                controller = new HomeController();
            }

            // 요구 사항 2,3,4번
            if (httpRequest.getPath().equals(USER_SIGNUP.getUrl())) {
                controller = new SignUpController();
            }

            // 요구 사항 5번
            if (httpRequest.getPath().equals(USER_LOGIN.getUrl())) {
                controller = new LoginController();
            }

            // 요구 사항 6번
            if (httpRequest.getPath().equals(USER_LIST.getUrl())) {
                controller = new ListController();
            }
            controller.execute(httpRequest, httpResponse);

        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

}