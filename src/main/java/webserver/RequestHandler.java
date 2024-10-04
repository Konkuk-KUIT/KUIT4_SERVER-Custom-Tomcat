package webserver;

import db.MemoryUserRepository;
import http.util.HttpRequestUtils;
import http.util.IOUtils;
import model.User;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


public class RequestHandler implements Runnable{
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());
    private static final String WEB_ROOT = "webapp";

    private MemoryUserRepository userRepository = MemoryUserRepository.getInstance();

    public RequestHandler(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()){
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            String requestLine = br.readLine();
            String[] tokens = requestLine.split(" ");
            String method = tokens[0];
            String requestPath = tokens[1];

            handleRequest(method, requestPath, br, dos);

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

    private void handleRequest(String method, String requestPath, BufferedReader br, DataOutputStream dos) throws IOException {
        if (method.equals("GET")) {
            // 요구사항 1
            if (isMainRequest(requestPath)) {
                handleFileResponse(dos, WEB_ROOT + "/index.html");
            }
            // 요구사항 2 - 회원가입 form
            if (isSignUpUserFormRequest(requestPath)) {
                handleFileResponse(dos, WEB_ROOT + "/user/form.html");
            }
            // 요구사항 2 - 회원가입 GET
            if (isUserSignUpRequest(requestPath)) {
                handleGetSignUpRequest(requestPath, dos);
            }
            // 요구사항 5 - 로그인 form
            if (isLoginUserFormRequest(requestPath)) {
                handleFileResponse(dos, WEB_ROOT + "/user/login.html");
            }
            // 요구사항 6 - user list 보기
            if (isUserListRequest(requestPath)) {
                handleUserListRequest(br, dos);
            }
        }

        if (method.equals("POST")) {
            // 요구사항 3 - 회원가입 POST
            if (isUserSignUpRequest(requestPath)) {
                handlePostSignUpRequest(br, dos);
            }
            // 요구사항 5 - 로그인 처리 POST
            if (isUserLoginRequest(requestPath)) {
                handlePostLoginRequest(br, dos);
            }
        }
    }

    private boolean isMainRequest(String requestPath) {
        return requestPath.equals("/") || requestPath.equals("/index.html");
    }

    private boolean isSignUpUserFormRequest(String requestPath) {
        return requestPath.equals("/user/form.html");
    }

    private boolean isUserSignUpRequest(String requestPath) {
        return requestPath.contains("/user/signup");
    }

    private boolean isLoginUserFormRequest(String requestPath) {
        return requestPath.equals("/user/login.html");
    }

    private boolean isUserLoginRequest(String requestPath) {
        return requestPath.equals("/user/login");
    }

    private boolean isUserListRequest(String requestPath) {
        return requestPath.equals("/user/userList");
    }

    private void handleFileResponse(DataOutputStream dos, String filePath) {
        byte[] body = readFile(filePath);
        if (body != null) {
            response200Header(dos, body.length);
            responseBody(dos, body);
        }
    }

    private void handleGetSignUpRequest(String requestPath, DataOutputStream dos) {
        if (requestPath.contains("?")) {
            String queryString = requestPath.substring(requestPath.indexOf("?") + 1);
            Map<String, String> queryParameter = HttpRequestUtils.parseQueryParameter(queryString);

            User user = createUserFromQuery(queryParameter);
            userRepository.addUser(user);
            logUser(user, queryParameter);

            // 요구사항 4
            response302Header(dos, "/index.html");
        }
    }

    private void handlePostSignUpRequest(BufferedReader br, DataOutputStream dos) throws IOException {
        int contentLength = 0;

        while (true) {
            String line = br.readLine();
            if (line.equals("")) {
                break;
            }
            if (line.startsWith("Content-Length")) {
                contentLength = Integer.parseInt(line.split(": ")[1]);
            }
        }

        String body = IOUtils.readData(br, contentLength);
        Map<String, String> queryParameter = HttpRequestUtils.parseQueryParameter(body);

        User user = createUserFromQuery(queryParameter);
        userRepository.addUser(user);
        logUser(user, queryParameter);

        // 요구사항 4
        response302Header(dos, "/index.html");
    }

    private void handlePostLoginRequest(BufferedReader br, DataOutputStream dos) throws IOException {
        int contentLength = 0;

        while (true) {
            String line = br.readLine();
            if (line.equals("")) {
                break;
            }
            if (line.startsWith("Content-Length")) {
                contentLength = Integer.parseInt(line.split(": ")[1]);
            }
        }

        String body = IOUtils.readData(br, contentLength);
        Map<String, String> queryParameter = HttpRequestUtils.parseQueryParameter(body);

        String userId = queryParameter.get("userId");
        String password = queryParameter.get("password");

        User user = userRepository.findUserById(userId);

        if (user != null && user.getPassword().equals(password)) {
            response302WithCookieHeader(dos, "/index.html", "logined=true");
        } else {
            response302Header(dos, "/user/logined_failed.html");
        }
    }

    private User createUserFromQuery(Map<String, String> queryParameter) {
        return new User(
                queryParameter.get("userId"),
                queryParameter.get("password"),
                queryParameter.get("name"),
                queryParameter.get("email")
        );
    }

    private void logUser(User user, Map<String, String> queryParameter) {
        queryParameter.forEach((key, value) -> log.log(Level.INFO, key + "=" + value));
        log.log(Level.INFO, "user: " + user);
        log.log(Level.INFO, "findUser: " + userRepository.findUserById(user.getUserId()));
    }

    private void handleUserListRequest(BufferedReader br, DataOutputStream dos) throws IOException {
        String cookie = getCookie(br);
        if (cookie != null && cookie.contains("logined=true")) {
            handleFileResponse(dos, WEB_ROOT + "/user/list.html");
        } else {
            response302Header(dos, "/user/login.html");
        }
    }

    private String getCookie(BufferedReader br) throws IOException {
        String line;
        while (!(line = br.readLine()).equals("")) {
            if (line.startsWith("Cookie")) {
                return line.substring("Cookie: ".length());
            }
        }
        return null;
    }

    private byte[] readFile(String filePath) {
        try {
            return Files.readAllBytes(Paths.get(filePath));
        } catch (IOException e) {
            log.log(Level.SEVERE, "File not found: " + filePath);
            return null;
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

    private void response302Header(DataOutputStream dos, String location) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: " + location + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302WithCookieHeader(DataOutputStream dos, String location, String cookie) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: " + location + "\r\n");
            dos.writeBytes("Set-Cookie: " + cookie + "\r\n");
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
