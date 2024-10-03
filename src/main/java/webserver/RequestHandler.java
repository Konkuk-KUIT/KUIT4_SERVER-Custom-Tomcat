package webserver;

import db.MemoryUserRepository;
import http.util.IOUtils;
import model.User;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestHandler implements Runnable{
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    public RequestHandler(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()){
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            // HTTP Request: start line
            String requestLine = br.readLine();

            String[] tokens = requestLine.split(" ");
            String method = tokens[0];
            String url = tokens[1];

            log.log(Level.INFO, "Request Method: " + method + ", URL: " + url);

            if ("GET".equals(method) && url.startsWith("/user/signup")) {
                // URL에서 쿼리스트링 추출 및 파싱
                String queryString = url.substring(url.indexOf("?") + 1);
                String[] parameters = queryString.split("&");

                String userId = null, password = null, name = null, email = null;
                for (String param : parameters) {
                    String[] keyValue = param.split("=");
                    switch (keyValue[0]) {
                        case "userId":
                            userId = keyValue[1];
                            break;
                        case "password":
                            password = keyValue[1];
                            break;
                        case "name":
                            name = keyValue[1];
                            break;
                        case "email":
                            email = keyValue[1];
                            break;
                    }
                }

                if (userId != null && password != null && name != null && email != null) {
                    User user = new User(userId, password, name, email);
                    MemoryUserRepository.getInstance().addUser(user);
                    log.log(Level.INFO, "New User Registered: " + user);
                }

                // 302 Redirect
                response302Header(dos, "/index.html", false);
                return;
            }

            if ("POST".equals(method) && url.startsWith("/user/signup")) {
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
                if (requestContentLength != 0) {
                    String requestBody = IOUtils.readData(br, requestContentLength);
                    log.log(Level.INFO, "회원정보: " + requestBody);
                    String[] parameters = requestBody.split("&");

                    String userId = null, password = null, name = null, email = null;
                    for (String param : parameters) {
                        String[] keyValue = param.split("=");
                        switch (keyValue[0]) {
                            case "userId":
                                userId = keyValue[1];
                                break;
                            case "password":
                                password = keyValue[1];
                                break;
                            case "name":
                                name = keyValue[1];
                                break;
                            case "email":
                                email = keyValue[1];
                                break;
                        }
                    }

                    if (userId != null && password != null && name != null && email != null) {
                        User user = new User(userId, password, name, email);
                        MemoryUserRepository.getInstance().addUser(user);
                        log.log(Level.INFO, "New User Registered: " + user);
                    }

                    // 302 Redirect
                    response302Header(dos, "/index.html", false);
                    return;

                }
            }

            if ("POST".equals(method) && url.startsWith("/user/login")) {
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
                if (requestContentLength != 0) {
                    String requestBody = IOUtils.readData(br, requestContentLength);
                    log.log(Level.INFO, "로그인정보: " + requestBody);
                    String[] parameters = requestBody.split("&");

                    String userId = null, password = null;
                    for (String param : parameters) {
                        String[] keyValue = param.split("=");
                        switch (keyValue[0]) {
                            case "userId":
                                userId = keyValue[1];
                                break;
                            case "password":
                                password = keyValue[1];
                                break;
                        }
                    }

                    if (userId != null && password != null) {
                        User loginedUser = MemoryUserRepository.getInstance().findUserById(userId);
                        if (loginedUser == null || !Objects.equals(loginedUser.getPassword(), password)) {
                            // 실패
                            log.log(Level.INFO, "실패했다!!!!!!!: " + userId + ", " + password);
                            response302Header(dos, "/user/login_failed.html", false);
                            return;
                        }
                        // 성공
                        // add Cookie
                        // 302 Redirect
                        response302Header(dos, "/index.html", true);
                        return;
                    }
                }

                response302Header(dos, "/user/login", false);
                return;
            }

            if (url.startsWith("/user/userList")) {
                boolean logined = false;
                String cookieHeader = null;

                while (true) {
                    final String line = br.readLine();
                    if (line.equals("")) {
                        break;
                    }
                    // header info
                    if (line.startsWith("Cookie")) {
                        cookieHeader = line;
                    }
                }

                // logined 쿠키 검사
                if (cookieHeader != null) {
                    if (cookieHeader.split(": ")[1].startsWith("logined=")) {
                        logined = cookieHeader.split(": ")[1].split("=")[1].equals("true");
                    }
                }

                // 로그인 여부에 따른 Redirect
                if (logined) {
                    response302Header(dos, "/user/list.html", true);
                } else {
                    response302Header(dos, "/index.html", false);
                }
                return;
            }

            // index.html 반환
            if (url.equals("/")) {
                url = "/index.html";
            }

            // CSS 파일 처리
            if (url.endsWith(".css")) {
                String filePath = "webapp" + url;
                byte[] body;
                if (Files.exists(Paths.get(filePath))) {
                    body = Files.readAllBytes(Paths.get(filePath));
                    response200Header(dos, body.length, "text/css");
                    responseBody(dos, body);
                }
                return;
            }

            // URL Redirect
            String filePath = "webapp" + url;
            byte[] body;
            if (Files.exists(Paths.get(filePath))) {
                body = Files.readAllBytes(Paths.get(filePath));
                response200Header(dos, body.length, "text/html");
                responseBody(dos, body);
            }

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, String contentType) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String location, boolean logined) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: " + location + "\r\n");
            if (logined)
                dos.writeBytes("Set-Cookie: logined=true \r\n");
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
