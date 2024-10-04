package webserver;

import db.MemoryUserRepository;
import http.util.HttpRequestUtils;
import model.User;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestHandler implements Runnable {
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    private final MemoryUserRepository memoryUserRepository = MemoryUserRepository.getInstance();

    public RequestHandler(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            String line = br.readLine();
            if (line == null) {
                return;
            }

            String[] tokens = line.split(" ");
            String url = tokens[1];

            byte[] body;

            // /index.html 요청 처리
            if ("/".equals(url) || "/index.html".equals(url)) {
                body = Files.readAllBytes(Paths.get("./webapp/index.html"));
                response200Header(dos, body.length);
                responseBody(dos, body);
                return;
            }

            // /user/form.html 요청 처리
            if ("/user/form.html".equals(url)) {
                body = Files.readAllBytes(Paths.get("./webapp/user/form.html"));
                response200Header(dos, body.length);
                responseBody(dos, body);
                return;
            }

            // /user/signup 요청 처리
            if (url.startsWith("/user/signup")) {
//                handleSignUp(url, dos);
                handlePostSignUp(br, dos);
                return;
            }

            // /user/login.html 요청 처리
            if ("/user/login.html".equals(url)) {
                body = Files.readAllBytes(Paths.get("./webapp/user/login.html"));
                response200Header(dos, body.length);
                responseBody(dos, body);
                return;
            }

            // /user/login 요청 처리
            if ("/user/login".equals(url)) {
                handleLogin(br, dos);
                return;
            }

            // /user/login_failed.html 요청 처리
            if ("/user/login_failed.html".equals(url)) {
                body = Files.readAllBytes(Paths.get("./webapp/user/login_failed.html"));
                response200Header(dos, body.length);
                responseBody(dos, body);
                return;
            }

            // /user/userList, /user/list.html 요청 처리
            // home에서는 /user/userList, 그 외에는 /user/list.html
            if ("/user/userList".equals(url) || "/user/list.html".equals(url)) {
                String cookie = getCookie(br);

                if ("logined=true".equals(cookie)) {
                    body = Files.readAllBytes(Paths.get("./webapp/user/list.html"));
                    response200Header(dos, body.length);
                    responseBody(dos, body);
                    return;
                }

                // 로그인되지 않은 경우 로그인 페이지로 리다이렉트
                response302RedirectHeader(dos, "/user/login.html");
                return;
            }

            // css 파일 요청 처리
            if (url.endsWith(".css")) {
                body = Files.readAllBytes(Paths.get("./webapp" + url));
                response200HeaderCss(dos, body.length);
                responseBody(dos, body);
                return;
            }

            // 404 처리
            body = "404 Not Found".getBytes();
            response404Header(dos, body.length);
            responseBody(dos, body);

        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    // 로그인 처리
    private void handleLogin(BufferedReader br, DataOutputStream dos) throws IOException {
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

        // request body 읽기
        char[] bodyData = new char[requestContentLength];
        br.read(bodyData, 0, requestContentLength);
        String body = new String(bodyData);

        Map<String, String> params = HttpRequestUtils.parseQueryParameter(body);

        String userId = params.get("userId");
        String password = params.get("password");

        if (userId == null || password == null) {
            // 로그인이 잘못되었을 때 에러 처리
            response302RedirectWithCookie(dos, "/user/login_failed.html", "logined=false");
            return;
        }

        User user = memoryUserRepository.findUserById(userId);

        if (user != null && user.getPassword().equals(password)) {
            response302RedirectWithCookie(dos, "/index.html", "logined=true");
        } else {
            response302RedirectWithCookie(dos, "/user/login_failed.html", "logined=false");
        }
    }

    // POST 방식 회원가입 처리
    private void handlePostSignUp(BufferedReader br, DataOutputStream dos) throws IOException {
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

        // request body 읽기
        char[] bodyData = new char[requestContentLength];
        br.read(bodyData, 0, requestContentLength);
        String body = new String(bodyData);

        Map<String, String> params = HttpRequestUtils.parseQueryParameter(body);

        User user = new User(params.get("userId"), params.get("password"), params.get("name"), params.get("email"));

        memoryUserRepository.addUser(user);

        // 302 리다이렉트 응답
        response302RedirectHeader(dos, "/index.html");
    }

    // 회원가입 처리
    private void handleSignUp(String url, DataOutputStream dos) throws IOException {
        // URL에서 쿼리스트링을 파싱하여 사용자 정보를 추출
        String queryString = url.split("\\?")[1];
        Map<String, String> params = HttpRequestUtils.parseQueryParameter(queryString);

        User user = new User(params.get("userId"), params.get("password"), params.get("name"), params.get("email"));

        memoryUserRepository.addUser(user);

        // 302 리다이렉트 응답
        response302RedirectHeader(dos, "/index.html");
    }

    // 쿠키 정보 추출
    private String getCookie(BufferedReader br) throws IOException {
        String cookie = null;

        while (true) {
            final String line = br.readLine();

            if (line.isEmpty()) {
                break;
            }

            if (line.startsWith("Cookie:")) {
                cookie = line.split(": ")[1];
            }
        }
        return cookie;
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

    private void response200HeaderCss(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/css;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response404Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 404 Not Found \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    // 302 리다이렉트 응답 처리
    private void response302RedirectHeader(DataOutputStream dos, String location) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: " + location + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    // 302 리다이렉트 응답 처리 (Cookie 포함)
    private void response302RedirectWithCookie(DataOutputStream dos, String location, String cookie) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: " + location + "\r\n");
            dos.writeBytes("Set-Cookie: " + cookie + "; Path=/\r\n");
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