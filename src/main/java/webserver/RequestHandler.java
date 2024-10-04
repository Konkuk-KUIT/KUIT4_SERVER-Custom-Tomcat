package webserver;

import db.MemoryUserRepository;
import db.Repository;
import http.util.IOUtils;
import model.User;
import strings.FilePath;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import static strings.FilePath.*;


public class RequestHandler implements Runnable{
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    private final Repository repository;
    private Controller controller = new ForwardController();

    public RequestHandler(Socket connection) {
        this.connection = connection;
        repository = MemoryUserRepository.getInstance();
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()){
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            byte[] body = null;

            HttpRequest httpRequest = HttpRequest.from(br);
            String reqURL = httpRequest.getUrl();

            HttpResponse httpResponse = new HttpResponse(dos);

            if (reqURL == null) {
                System.out.println("Request URL is null");
            }

            // 요구 사항 1번
            if (httpRequest.getMethod().equals("GET") && httpRequest.getUrl().endsWith(".html")) {
                controller = new ForwardController();
            }

            if (httpRequest.getUrl().equals("/")) {
                controller = new HomeController();
            }

            // 요구 사항 2,3,4번
            if (httpRequest.getUrl().equals("/user/signup")) {
                controller = new SignUpController();
            }

            // 요구 사항 5번
            if (httpRequest.getUrl().equals("/user/login")) {
                controller = new LoginController();
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