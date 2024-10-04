package webserver;

import db.MemoryUserRepository;
import http.constant.*;
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
            HttpMethod method = HttpMethod.valueOf(tokens[0]);
            String requestPath = tokens[1];

            handleRequest(method, requestPath, br, dos);

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

    private void handleRequest(HttpMethod method, String requestPath, BufferedReader br, DataOutputStream dos) throws IOException {
        if (method == HttpMethod.GET) {
            // 요구사항 1
            if (isMainRequest(requestPath)) {
                handleFileResponse(dos, WEB_ROOT + UrlPath.INDEX.getPath());
            }
            // 요구사항 2 - 회원가입 form
            else if (isSignUpUserFormRequest(requestPath)) {
                handleFileResponse(dos, WEB_ROOT + UrlPath.SIGN_UP_FORM.getPath());
            }
            // 요구사항 2 - 회원가입 GET
            else if (isUserSignUpRequest(requestPath)) {
                handleGetSignUpRequest(requestPath, dos);
            }
            // 요구사항 5 - 로그인 form
            else if (isLoginUserFormRequest(requestPath)) {
                handleFileResponse(dos, WEB_ROOT + UrlPath.LOGIN_FORM.getPath());
            }
            // 요구사항 6 - user list 보기
            else if (isUserListRequest(requestPath)) {
                handleUserListRequest(br, dos);
            }
            // 요구사항 7 - css 적용
            else if (isCssRequest(requestPath)) {
                handleCssResponse(dos, requestPath);
            }
        }

        else if (method == HttpMethod.POST) {
            // 요구사항 3 - 회원가입 POST
            if (isUserSignUpRequest(requestPath)) {
                handlePostSignUpRequest(br, dos);
            }
            // 요구사항 5 - 로그인 처리 POST
            else if (isUserLoginRequest(requestPath)) {
                handlePostLoginRequest(br, dos);
            }
        }
    }

    private boolean isMainRequest(String requestPath) {
        return requestPath.equals("/") || requestPath.equals(UrlPath.INDEX.getPath());
    }

    private boolean isSignUpUserFormRequest(String requestPath) {
        return requestPath.equals(UrlPath.SIGN_UP_FORM.getPath());
    }

    private boolean isUserSignUpRequest(String requestPath) {
        return requestPath.contains(UrlPath.SIGN_UP.getPath());
    }

    private boolean isLoginUserFormRequest(String requestPath) {
        return requestPath.equals(UrlPath.LOGIN_FORM.getPath());
    }

    private boolean isUserLoginRequest(String requestPath) {
        return requestPath.equals(UrlPath.LOGIN.getPath());
    }

    private boolean isUserListRequest(String requestPath) {
        return requestPath.equals(UrlPath.USER_LIST.getPath());
    }

    private boolean isCssRequest(String requestPath) {
        return requestPath.endsWith(UrlPath.CSS.getPath());
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
            response302Header(dos, UrlPath.INDEX.getPath());
        }
    }

    private void handlePostSignUpRequest(BufferedReader br, DataOutputStream dos) throws IOException {
        int contentLength = 0;

        while (true) {
            String line = br.readLine();
            if (line.equals("")) {
                break;
            }
            if (line.startsWith(HttpHeader.CONTENT_LENGTH.getHeader())) {
                contentLength = Integer.parseInt(line.split(": ")[1]);
            }
        }

        String body = IOUtils.readData(br, contentLength);
        Map<String, String> queryParameter = HttpRequestUtils.parseQueryParameter(body);

        User user = createUserFromQuery(queryParameter);
        userRepository.addUser(user);
        logUser(user, queryParameter);

        // 요구사항 4
        response302Header(dos, UrlPath.INDEX.getPath());
    }

    private void handlePostLoginRequest(BufferedReader br, DataOutputStream dos) throws IOException {
        int contentLength = 0;

        while (true) {
            String line = br.readLine();
            if (line.equals("")) {
                break;
            }
            if (line.startsWith(HttpHeader.CONTENT_LENGTH.getHeader())) {
                contentLength = Integer.parseInt(line.split(": ")[1]);
            }
        }

        String body = IOUtils.readData(br, contentLength);
        Map<String, String> queryParameter = HttpRequestUtils.parseQueryParameter(body);

        String userId = queryParameter.get(UserQueryKey.USER_ID.getKey());
        String password = queryParameter.get(UserQueryKey.PASSWORD.getKey());

        User user = userRepository.findUserById(userId);

        if (user != null && user.getPassword().equals(password)) {
            response302WithCookieHeader(dos, UrlPath.INDEX.getPath(), "logined=true");
        } else {
            response302Header(dos, UrlPath.LOGIN_FAILED.getPath());
        }
    }

    private User createUserFromQuery(Map<String, String> queryParameter) {
        return new User(
                queryParameter.get(UserQueryKey.USER_ID.getKey()),
                queryParameter.get(UserQueryKey.PASSWORD.getKey()),
                queryParameter.get(UserQueryKey.NAME.getKey()),
                queryParameter.get(UserQueryKey.EMAIL.getKey())
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
            handleFileResponse(dos, WEB_ROOT + UrlPath.LIST.getPath());
        } else {
            response302Header(dos, UrlPath.LOGIN_FORM.getPath());
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

    private void handleCssResponse(DataOutputStream dos, String requestPath) {
        String filePath = WEB_ROOT + requestPath;
        byte[] body = readFile(filePath);
        if (body != null) {
            response200CssHeader(dos, body.length);
            responseBody(dos, body);
        } else {
            log.log(Level.SEVERE, "CSS file not found: " + requestPath);
        }
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
            dos.writeBytes(HttpStatusCode.OK.getStatus() + " \r\n");
            dos.writeBytes(HttpHeader.CONTENT_TYPE.getHeader() + ": text/html;charset=utf-8\r\n");
            dos.writeBytes(HttpHeader.CONTENT_LENGTH.getHeader() + ": " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String location) {
        try {
            dos.writeBytes(HttpStatusCode.FOUND.getStatus() + " \r\n");
            dos.writeBytes(HttpHeader.LOCATION.getHeader() + ": " + location + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302WithCookieHeader(DataOutputStream dos, String location, String cookie) {
        try {
            dos.writeBytes(HttpStatusCode.FOUND.getStatus() + " \r\n");
            dos.writeBytes(HttpHeader.LOCATION.getHeader() + ": " + location + "\r\n");
            dos.writeBytes(HttpHeader.SET_COOKIE.getHeader() + ": " + cookie + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response200CssHeader(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes(HttpStatusCode.OK.getStatus() + " \r\n");
            dos.writeBytes(HttpHeader.CONTENT_TYPE.getHeader() + ": text/css\r\n");
            dos.writeBytes(HttpHeader.CONTENT_LENGTH.getHeader() + ": " + lengthOfBodyContent + "\r\n");
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
