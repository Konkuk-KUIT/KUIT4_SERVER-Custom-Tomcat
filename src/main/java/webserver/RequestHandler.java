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
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestHandler implements Runnable{
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());
    private static final String WEBAPP = "webapp";

    public RequestHandler(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {

            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            DataOutputStream dos = new DataOutputStream(out);

            String request = br.readLine();
            if (request == null || request.isEmpty()) {
                return;
            }
            log.log(Level.INFO, "request : " + request);

            String[] tokens = request.split(" ");
            String method = tokens[0]; // "GET", "POST"
            String path = tokens[1];   // "/index.html", "/user/signup" ..

            // 요구사항 2 & 3: GET or POST 회원가입
            if (path.startsWith("/user/signup")) {
                if (method.equals("GET") && path.contains("?")) {
                    String queryString = path.substring(path.indexOf("?") + 1);
                    Map<String, String> queryParameter = HttpRequestUtils.parseQueryParameter(queryString);
                    signUpUser(queryParameter, dos);
                    return;
                }
                else if (method.equals("POST")) {
                    // todo requestContentLength method
                    int requestContentLength = 0;
                    while (true) {
                        final String line = br.readLine();
                        if (line.isEmpty()) {
                            break;
                        }
                        // header info
                        if (line.startsWith("Content-Length")) {
                            requestContentLength = Integer.parseInt(line.split(": ")[1]);
                        }
                    }
                    String body = IOUtils.readData(br, requestContentLength);
                    Map<String, String> queryParameter = HttpRequestUtils.parseQueryParameter(body);
                    signUpUser(queryParameter, dos);
                    return;
                }
            }

            // 기본 파일 처리 부분
            if ("/".equals(path)) {
                path = "/index.html";
            }

            File file = new File(WEBAPP + path);

            if (file.exists()) {
                byte[] body = Files.readAllBytes(Paths.get(file.getPath()));
                response200Header(dos, body.length);
                responseBody(dos, body);
            } else {
                response404Header(dos);
            }

        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void signUpUser(Map<String, String> queryParameter, DataOutputStream dos) {
        queryParameter.forEach((key, value) -> log.log(Level.INFO, key + " : " + value));

        String userId = queryParameter.get("userId");
        String password = queryParameter.get("password");
        String name = queryParameter.get("name");
        String email = queryParameter.get("email");

        User user = new User(userId, password, name, email);
        log.log(Level.INFO, "user: " + user);

        Repository repository = MemoryUserRepository.getInstance();
        repository.addUser(user);
        log.log(Level.INFO, "user: " + repository.findUserById(userId));

        // 302 redirect
        response302Header(dos, "/index.html");
    }

    private void response302Header(DataOutputStream dos, String redirectUrl) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: " + redirectUrl + "\r\n");
            dos.writeBytes("\r\n");
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
    private void response404Header(DataOutputStream dos) {
        try {
            String body = "<h1>404 Not Found</h1>";
            dos.writeBytes("HTTP/1.1 404 Not Found \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + body.length() + "\r\n");
            dos.writeBytes("\r\n");
            dos.writeBytes(body);
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
