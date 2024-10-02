package webserver;

import db.MemoryUserRepository;
import http.util.HttpRequestUtils;
import http.util.IOUtils;
import model.User;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestHandler implements Runnable{
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());
    private static final String WEBAPP_DIR = "webapp";
    private MemoryUserRepository userRepository;
    // 여기서 repository DI 받음
    public RequestHandler(Socket connection , MemoryUserRepository userRepository) {
        this.connection = connection;
        this.userRepository = userRepository;
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()){
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);


            byte[] body;
            // GET /index.html
            String request = br.readLine();
            if (request == null || request.isEmpty()) {
                return;
            }

            // 여기서부터 헤더를 읽고 Content-Length를 파악
            // br의 offset을 body쪽으로 이동
            int requestContentLength = 0; // Content-Length 초기화
            String cookieHeader = null;
            while (true) {
                final String line = br.readLine();
                if (line.equals("")) {
                    break; // 빈 줄을 만나면 헤더 끝
                }
                // 헤더 정보 처리
                if (line.startsWith("Content-Length")) {
                    requestContentLength = Integer.parseInt(line.split(": ")[1]);
                }
                // 쿠키 헤더들
                if (line.startsWith("Cookie")) {
                    cookieHeader = line;
                }
            }

            // request body
            HashMap<String, String> bodyParams = new HashMap<>();
            if (requestContentLength > 0) {
                String bodyContent = IOUtils.readData(br, requestContentLength);
                bodyParams = (HashMap<String, String>) HttpRequestUtils.parseQueryParameter(bodyContent);
            }


            // 요청 라인 파싱
            String[] tokens = request.split(" ");

            String method = tokens[0];
            String url = tokens[1]; // /index.html?name=John&age=30
            String protocol = tokens[2]; // HTTP/1.1

            // URL에서 경로와 쿼리 스트링 분리
            String path = url.split("\\?")[0];  // /index.html
            String queryString = null;
            HashMap<String, String> queryParams = new HashMap<>();
            if (url.contains("?")) {
                queryString = url.split("\\?")[1];  // name=John&age=30
                queryParams = (HashMap<String, String>) HttpRequestUtils.parseQueryParameter(queryString);
            }


            if (method.equals("GET") && path.equals("/user/signup")) {
                // 요구사항2 : get 요청으로 회원가입할때
                String userId = queryParams.get("userId");
                String password = queryParams.get("password");
                String name = queryParams.get("name");
                String email = queryParams.get("email");
                User user = new User(userId, password, name, email);
                if (userRepository.findUserById(userId) == null){
                    userRepository.addUser(user);
                }
                response302Redirect(dos, "/index.html");
            } else if (method.equals("POST") && path.equals("/user/signup")) {
                // 요구사항3 : POST 요청으로 회원가입할때
                String userId = bodyParams.get("userId");
                String password = bodyParams.get("password");
                String name = bodyParams.get("name");
                String email = bodyParams.get("email");
                User user = new User(userId, password, name, email);
                if (userRepository.findUserById(userId) == null){
                    userRepository.addUser(user);
                }
                response302Redirect(dos, "/index.html");
            } else if (method.equals("POST") && path.equals("/user/login")) {
                // 요구사항5 : 로그인 요청
                String userId = bodyParams.get("userId");
                String password = bodyParams.get("password");
                User user = userRepository.findUserById(userId);
                if (user != null){
                    if (password.equals(user.getPassword())){
                        response302RedirectWithCookie(dos, "/index.html","logined=true");
                    }
                }
                response302Redirect(dos, "/user/login_failed.html");
            } else if (path.equals("/user/userList")) {
                // 요구사항 6 :
                if (cookieHeader == null || !(cookieHeader.contains("logined=true"))) {
                    response302Redirect(dos, "/index.html");
                }
                viewStaticFile("/user/list.html", dos);
            } else if (path.equals("/")){
                // 요구사항1 : / 요청으로 index.html 이동
                path = "/index.html";
                viewStaticFile(path, dos);
            // 지정된 패쓰가 없으면 그래도 정적 파일
            }  else {
                viewStaticFile(path, dos);
            }



        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

    private void viewStaticFile(String path, DataOutputStream dos) throws IOException {
        byte[] body;
        String filePath = WEBAPP_DIR + path;
        File file = new File(filePath);
        if (file.exists()) {
            body = Files.readAllBytes(Paths.get(filePath));
            response200Header(dos, body.length);
            responseBody(dos, body);
        }
        else {
            log.log(Level.INFO,"파일 없음!!!!!!");
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

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302Redirect(DataOutputStream dos, String url) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found\r\n");
            dos.writeBytes("Location: " + url + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302RedirectWithCookie(DataOutputStream dos, String url, String cookie) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found\r\n");
            dos.writeBytes("Set-Cookie: " + cookie + "\r\n");
            dos.writeBytes("Location: " + url + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

}
