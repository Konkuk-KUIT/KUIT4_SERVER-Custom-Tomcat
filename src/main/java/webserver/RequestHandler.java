package webserver;

import db.MemoryUserRepository;
import db.Repository;
import http.util.HttpRequestUtils;
import model.User;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestHandler implements Runnable {
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());
    private static final String WEBAPP_PATH = "webapp/";

    public RequestHandler(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);


            // 요청 읽기
            String requestLine = br.readLine();
            log.log(Level.INFO, "Request Line: " + requestLine);

            // 요청 path 읽기
            String[] requestParts = requestLine.split(" ");
            String method = requestParts[0];
            String path = requestParts[1];   // 0: HTTP Method, 1: request URL, 2: HTTP Version
            log.log(Level.INFO, "METHOD: " + method);
            log.log(Level.INFO, "Request Path: " + path);

            // 요구사항 1
            //80 포트로 들어오거나, index.html로 주소가 들어올 경우 index.html을 출력하도록 함
            if (path.equals("/") || path.equals("/index.html")) {
                byte[] body = Files.readAllBytes(Paths.get(WEBAPP_PATH + "index.html"));
                response200Header(dos, body.length);
                responseBody(dos, body);
            }

            // 요구사항 2
            if (path.equals("/user/form.html")) {
                byte[] body = Files.readAllBytes(Paths.get(WEBAPP_PATH + "/user/form.html"));
                response200Header(dos, body.length);
                responseBody(dos, body);
            }

            if(path.contains("/user/signup")) {
                log.log(Level.INFO, "Signup request received");

                if(path.contains("?")) {
                    // URL 에서 쿼리 파라미터 분리
                    String queryString = path.substring(path.indexOf("?") + 1);
                    Map<String, String> queryParameter = HttpRequestUtils.parseQueryParameter(queryString);

                    // 파라미터들 확인
                    queryParameter.forEach((key, value) -> log.log(Level.INFO, key + "=" + value));

                    String userId = queryParameter.get("userId");
                    String password = queryParameter.get("password");
                    String name = queryParameter.get("name");
                    String email = queryParameter.get("email");

                    User user = new User(userId, password, name, email);
                    log.log(Level.INFO, "user: " + user);

                    Repository repository = MemoryUserRepository.getInstance();
                    repository.addUser(user);
                    log.log(Level.INFO, "findUser: " + repository.findUserById(userId));

                    byte[] body = Files.readAllBytes(Paths.get(WEBAPP_PATH + "index.html"));
                    response200Header(dos, body.length);
                    responseBody(dos, body);
                }
            }


        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
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

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

}
