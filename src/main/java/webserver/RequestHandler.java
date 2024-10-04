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

import static enumClass.Url.*;
import static enumClass.HttpMethod.*;
import static enumClass.HttpHeader.*;
import static enumClass.StatusCode.*;
import static enumClass.UserQueryKey.*;

public class RequestHandler implements Runnable {
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

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

            // Requirement 2 & 3: GET or POST user signup
            if (path.startsWith(USER_SIGNUP.getValue())) {
                if (method.equals(GET.getValue()) && path.contains("?")) {
                    String queryString = path.substring(path.indexOf("?") + 1);
                    Map<String, String> queryParameter = HttpRequestUtils.parseQueryParameter(queryString);
                    signUpUser(queryParameter, dos);
                    return;
                } else if (method.equals(POST.getValue())) {
                    int requestContentLength = 0;
                    while (true) {
                        final String line = br.readLine();
                        if (line.isEmpty()) {
                            break;
                        }
                        if (line.startsWith(CONTENT_LENGTH.getValue())) {
                            requestContentLength = Integer.parseInt(line.split(": ")[1]);
                        }
                    }
                    String body = IOUtils.readData(br, requestContentLength);
                    Map<String, String> queryParameter = HttpRequestUtils.parseQueryParameter(body);
                    signUpUser(queryParameter, dos);
                    return;
                }
            }

            // Requirement 5: User login
            if (path.startsWith(USER_LOGIN.getValue()) && method.equals(POST.getValue())) {
                int requestContentLength = 0;
                while (true) {
                    final String line = br.readLine();
                    if (line.isEmpty()) {
                        break;
                    }
                    if (line.startsWith(CONTENT_LENGTH.getValue())) {
                        requestContentLength = Integer.parseInt(line.split(": ")[1]);
                    }
                }
                String body = IOUtils.readData(br, requestContentLength);
                Map<String, String> queryParameter = HttpRequestUtils.parseQueryParameter(body);
                String userId = queryParameter.get(USERID.getValue());
                String password = queryParameter.get(PASSWORD.getValue());

                Repository repository = MemoryUserRepository.getInstance();
                User user = repository.findUserById(userId);

                if (user != null && user.getPassword().equals(password)) {
                    // Successful login & set cookie
                    response302HeaderWithCookie(dos, INDEX_PAGE.getValue(), "logined=true");
                } else {
                    // Failed login
                    response302Header(dos, USER_LOGIN_FAILED_PAGE.getValue());
                }

                return;
            }

            // Requirement 6: User list for logged-in users
            if (method.equals(GET.getValue()) && path.equals(USER_USERLIST.getValue())) {

                boolean isLogined = false;

                while (true) {
                    final String line = br.readLine();
                    if (line.isEmpty()) {
                        break;
                    }

                    if (line.startsWith(COOKIE.getValue())) {
                        String loginState = line.split(": ")[1];

                        if (loginState.equals("logined=true")) {
                            isLogined = true;
                        }

                        if (loginState.equals("logined=false")) {
                            isLogined = false;
                        }
                    }
                }

                if (isLogined) {
                    response302UserListHeader(dos);
                } else {
                    response302LoginHeader(dos);
                }
            }

            // Handle basic file requests
            if (ROOT.getValue().equals(path)) {
                path = INDEX_PAGE.getValue();
            }

            File file = new File(WEBAPP.getValue() + path);

            if (file.exists()) {
                byte[] body = Files.readAllBytes(Paths.get(file.getPath()));
                String contentType = getContentType(path);

                response200Header(dos, body.length, contentType);
                responseBody(dos, body);
            } else {
                response404Header(dos);
            }

        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private String getContentType(String path) {
        if (path.endsWith(".html")) {
            return "text/html;charset=utf-8";
        } else if (path.endsWith(STYLE_CSS.getValue())) {
            return "text/css;charset=utf-8";
        } else if (path.endsWith(".js")) {
            return "application/javascript;charset=utf-8";
        } else if (path.endsWith(".png")) {
            return "image/png";
        } else if (path.endsWith(".jpg") || path.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (path.endsWith(".gif")) {
            return "image/gif";
        } else {
            return "text/plain;charset=utf-8";
        }
    }

    private void response302UserListHeader(DataOutputStream dos) {
        try {
            dos.writeBytes("HTTP/1.1 " + FOUND.getValue() + " \r\n");
            dos.writeBytes(LOCATION.getValue() + ": " + USER_LIST_PAGE.getValue() + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302LoginHeader(DataOutputStream dos) {
        try {
            dos.writeBytes("HTTP/1.1 " + FOUND.getValue() + " \r\n");
            dos.writeBytes(LOCATION.getValue() + ": " + USER_LOGIN_PAGE.getValue() + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void signUpUser(Map<String, String> queryParameter, DataOutputStream dos) {
        queryParameter.forEach((key, value) -> log.log(Level.INFO, key + " : " + value));

        String userId = queryParameter.get(USERID.getValue());
        String password = queryParameter.get(PASSWORD.getValue());
        String name = queryParameter.get(NAME.getValue());
        String email = queryParameter.get(EMAIL.getValue());

        User user = new User(userId, password, name, email);
        log.log(Level.INFO, "user: " + user);

        Repository repository = MemoryUserRepository.getInstance();
        repository.addUser(user);
        log.log(Level.INFO, "user: " + repository.findUserById(userId));

        // 302 redirect
        response302Header(dos, INDEX_PAGE.getValue());
    }

    private void response302HeaderWithCookie(DataOutputStream dos, String redirectUrl, String cookie) {
        try {
            dos.writeBytes("HTTP/1.1 " + FOUND.getValue() + " \r\n");
            dos.writeBytes(LOCATION.getValue() + ": " + redirectUrl + "\r\n");
            dos.writeBytes(SET_COOKIE.getValue() + ": " + cookie + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String redirectUrl) {
        try {
            dos.writeBytes("HTTP/1.1 " + FOUND.getValue() + " \r\n");
            dos.writeBytes(LOCATION.getValue() + ": " + redirectUrl + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, String contentType) {
        try {
            dos.writeBytes("HTTP/1.1 " + OK.getValue() + " \r\n");
            dos.writeBytes(CONTENT_TYPE.getValue() + ": " + contentType + "\r\n");
            dos.writeBytes(CONTENT_LENGTH.getValue() + ": " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response404Header(DataOutputStream dos) {
        try {
            String body = "<h1>404 Not Found</h1>";
            dos.writeBytes("HTTP/1.1 " + NOTFOUND.getValue() + " \r\n");
            dos.writeBytes(CONTENT_TYPE.getValue() + ": text/html;charset=utf-8\r\n");
            dos.writeBytes(CONTENT_LENGTH.getValue() + ": " + body.length() + "\r\n");
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
