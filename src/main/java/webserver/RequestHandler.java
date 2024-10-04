package webserver;

import db.MemoryUserRepository;
import http.HttpRequest;
import http.HttpResponse;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import Controller.*;

//클라이언트 요청을 처리하는 스레드 - 연결 소켓 처리
//서버로 들어오는 연결을 처리하고, 클라이언트에게 응답을 반환하는 역할
public class RequestHandler implements Runnable {
    Socket connection;
    private Controller controller;
    private final MemoryUserRepository userRepository;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    public RequestHandler(Socket connection, MemoryUserRepository userRepository) {
        this.connection = connection;
        this.userRepository = userRepository;
    }

    @Override
    public void run() {             //요청을 읽고, 간단한 "Hello World" 응답을 반환
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {        //in*out stream
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);


            HttpRequest httpRequest = HttpRequest.from(br);             //HttpRequest 객체 생성
            HttpResponse httpResponse = new HttpResponse(dos);

            
            // 요구 사항 1번
            if (httpRequest.getMethod().equals("GET") && httpRequest.getPath().endsWith(".html")) {
                controller = new ForwardController();
            }

            if (httpRequest.getPath().equals("/")) {
                controller = new HomeController();
            }

            // 요구 사항 2,3,4번
            if (httpRequest.getPath().equals("/user/signup")) {
                controller = new SignUpController();
            }

            // 요구 사항 5번
            if (httpRequest.getPath().equals("/user/login")) {
                controller = new LoginController();
            }

            // 요구 사항 6번
            if (httpRequest.getPath().equals("/user/userList")) {
                controller = new ListController();
            }
            controller.execute(httpRequest, httpResponse);

        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage());
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
    }
}



