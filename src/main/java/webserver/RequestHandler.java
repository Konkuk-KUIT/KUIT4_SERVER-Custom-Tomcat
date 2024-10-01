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

            if ("/".equals(url) || "/index.html".equals(url)) {
                // /index.html 요청 처리
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
                handleSignUp(url, dos);
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

    // 회원가입 처리
    private void handleSignUp(String url, DataOutputStream dos) throws IOException {
        // URL에서 쿼리스트링을 파싱하여 사용자 정보를 추출
        String queryString = url.split("\\?")[1];
        Map<String, String> params = HttpRequestUtils.parseQueryParameter(queryString);

        // User 객체 생성
        User user = new User(params.get("userId"), params.get("password"), params.get("name"), params.get("email"));

        // MemoryUserRepository에 사용자 정보 저장
        memoryUserRepository.addUser(user);

        // 302 리다이렉트 응답
        response302RedirectHeader(dos, "/index.html");
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

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

}
