package webserver;

import controller.*;
import db.Repository;
import http.request.HttpRequest;
import http.response.HttpResponse;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestHandler implements Runnable{
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    private final Repository repository;
    private Controller controller = new ForwardController();

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
                controller = new ForwardController();
            }

            if (httpRequest.getUrl().equals("/")) {
                controller = new HomeController();
            }

            // 요구 사항 2, 3, 4번
            if (httpRequest.getUrl().equals("/user/signup") && httpRequest.getMethod().isEqual("POST")) {
                controller = new SignUpController(repository);
            }

            // 요구 사항 5번
            if (httpRequest.getUrl().equals("/user/login")) {
                controller = new LoginController(repository);
            }

            // 요구 사항 6번
            if (httpRequest.getUrl().equals("/user/userList")) {
                controller = new ListController();
            }

            controller.execute(httpRequest, httpResponse);

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }
}
