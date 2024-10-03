package webserver;

import controller.*;
import db.MemoryUserRepository;
import http.request.HttpMethod;
import http.request.HttpRequest;
import http.response.HttpResponse;
import http.util.HttpRequestUtils;
import model.User;

import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static http.request.HttpMethod.GET;

public class RequestHandler implements Runnable{
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());
    Controller controller;

    public RequestHandler(Socket connection) {
        this.connection = connection;
    }


    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()){

            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            // Header 분석
            HttpRequest httpRequest = HttpRequest.from(br);
            HttpResponse httpResponse = new HttpResponse(dos);

            if (httpRequest.getMethod().isEqual(GET) && (httpRequest.getUrl().endsWith(".html") || httpRequest.getUrl().endsWith(".css"))) {
                controller = new ForwardController();
            }

            if (httpRequest.getUrl().equals("/")) {
                controller = new HomeController();
            }

            if (httpRequest.getUrl().equals("/user/signup")) {
                controller = new SignUpController();
            }

            if (httpRequest.getUrl().equals("/user/login")) {
                controller = new LoginController();
            }

            if (httpRequest.getUrl().equals("/user/userList")) {
                controller = new ListController();
            }
            controller.execute(httpRequest, httpResponse);

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }



}
