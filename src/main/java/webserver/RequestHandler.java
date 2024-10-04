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
            if (isMainRequest(requestPath)) {
                handleFileResponse(dos, WEB_ROOT + "/index.html");
            } else if (isSignUpUserFormRequest(requestPath)) {
                handleFileResponse(dos, WEB_ROOT + "/user/form.html");
            } else if (isUserSignUpRequest(requestPath)) {
                handleGetSignUpRequest(requestPath, dos);
            }
        } else if (method.equals("POST")) {
            if (isUserSignUpRequest(requestPath)) {
                handlePostSignupRequest(br, dos);
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

            response302Header(dos);
        }
    }

    private void handlePostSignupRequest(BufferedReader br, DataOutputStream dos) throws IOException {
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

        response302Header(dos);
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

    private void response302Header(DataOutputStream dos) {
        try {
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
