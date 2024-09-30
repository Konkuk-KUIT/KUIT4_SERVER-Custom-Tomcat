package webserver;

import db.MemoryUserRepository;
import db.Repository;
import http.util.HttpRequestUtils;
import http.util.IOUtils;
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
            if (method.equals("GET") && path.equals("/user/form.html")) {
                byte[] body = Files.readAllBytes(Paths.get(WEBAPP_PATH + "/user/form.html"));
                response200Header(dos, body.length);
                responseBody(dos, body);
            }

            if (method.equals("GET") && path.contains("/user/signup")) {
                log.log(Level.INFO, "Signup request received GET Method");

                if (path.contains("?")) {
                    // URL 에서 쿼리 파라미터 분리
                    String queryString = subtractQueryParameters(path);
                    Map<String, String> queryParameters = HttpRequestUtils.parseQueryParameter(queryString);

                    // 파라미터들 확인
                    queryParameters.forEach((key, value) -> log.log(Level.INFO, key + "=" + value));

                    String userId = queryParameters.get("userId");
                    String password = queryParameters.get("password");
                    String name = queryParameters.get("name");
                    String email = queryParameters.get("email");

                    User user = new User(userId, password, name, email);
                    log.log(Level.INFO, "user: " + user);

                    Repository repository = MemoryUserRepository.getInstance();
                    repository.addUser(user);
                    log.log(Level.INFO, "findUser: " + repository.findUserById(userId));

                    // 요구사항 4 적용
                    response302Header(dos);
                }
            }

            // 요구사항 3
            // POST 방식으로 전송하면 쿼리 파라미터가 사라진다. 즉 parseQueryParameter를 통해서 값을 얻어올 수 없다.
            // 대신 POST 방식에서는 이 쿼리 파라미터가 body 안에 들어간다.
            if (method.equals("POST") && path.equals("/user/signup")) {
                log.log(Level.INFO, "Signup request received Post Method");

                int requestContentLength = 0;

                while (true) {
                    final String line = br.readLine();
                    if (line.equals("")) {
                        break;
                    }

                    // header info
                    if (line.startsWith("Content-Length")) {
                        requestContentLength = Integer.parseInt(line.split(": ")[1]);
                    }
                }

                String requestBody = IOUtils.readData(br, requestContentLength);
                log.log(Level.INFO, "reqeustBody: " + requestBody);

                String queryString = subtractQueryParameters(requestBody);
                Map<String, String> queryParameters = HttpRequestUtils.parseQueryParameter(queryString);

                // 파라미터들 확인
                queryParameters.forEach((key, value) -> log.log(Level.INFO, key + "=" + value));

                String userId = queryParameters.get("userId");
                String password = queryParameters.get("password");
                String name = queryParameters.get("name");
                String email = queryParameters.get("email");

                User user = new User(userId, password, name, email);
                log.log(Level.INFO, "user: " + user);

                Repository repository = MemoryUserRepository.getInstance();
                repository.addUser(user);
                log.log(Level.INFO, "findUser: " + repository.findUserById(userId));

                // 요구사항 4 적용
                response302Header(dos);
            }


        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private String subtractQueryParameters(String path) {
        return path.substring(path.indexOf("?") + 1);
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

    // 요구사항 4
    private void response302Header(DataOutputStream dos) {
        try{
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: /index.html\r\n");
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
