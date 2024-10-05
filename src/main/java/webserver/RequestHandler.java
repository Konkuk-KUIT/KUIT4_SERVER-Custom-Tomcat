package webserver;

import controller.*;
import db.MemoryUserRepository;
import db.Repository;
import enums.Url;
import http.util.HttpRequest;
import http.util.HttpResponse;
import http.util.IOUtils;
import model.User;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

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

            // Header 분석
            HttpRequest httpRequest = HttpRequest.from(br);
            HttpResponse httpResponse = new HttpResponse(dos);


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

            /*
            // HTTP Request: start line
            String requestLine = br.readLine();

            String[] tokens = requestLine.split(" ");
            String method = tokens[0];
            String url = tokens[1];


            log.log(Level.INFO, "Request Method: " + method + ", URL: " + url);

            // 회원가입 (GET)
            if ("GET".equals(method) && url.startsWith(Url.SIGNUP.getPath())) {
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
                response302Header(dos, Url.INDEX_HTML.getPath(), false);
                return;
            }

            // 회원가입 (POST)
            if ("POST".equals(method) && url.startsWith(Url.SIGNUP.getPath())) {
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
                    response302Header(dos, Url.INDEX_HTML.getPath(), false);
                    return;

                }
            }

            // 로그인
            if ("POST".equals(method) && url.startsWith(Url.LOGIN.getPath())) {
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
                            response302Header(dos, Url.LOGIN_FAILED_HTML.getPath(), false);
                            return;
                        }
                        // 성공
                        // add Cookie
                        // 302 Redirect
                        response302Header(dos, Url.INDEX_HTML.getPath(), true);
                        return;
                    }
                }

                response302Header(dos, Url.LOGIN.getPath(), false);
                return;
            }

            // 사용자 목록
            if (url.startsWith(Url.USER_LIST.getPath())) {
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
                    response302Header(dos, Url.USER_LIST_HTML.getPath(), true);
                } else {
                    response302Header(dos, Url.INDEX_HTML.getPath(), false);
                }
                return;
            }

            // index.html 반환
            if (url.equals("/")) {
                url = Url.SIGNUP.getPath();
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

             */

        } catch (Exception e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, String contentType) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: " + contentType + ";charset=utf-8\r\n");
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
