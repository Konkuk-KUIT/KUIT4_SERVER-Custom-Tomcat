package webserver;

import db.MemoryUserRepository;
import http.util.HttpRequestUtils;
import http.util.IOUtils;
import model.User;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestHandler implements Runnable{
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    // 정적 리소스 root 디렉토리
    private static final String WEB_ROOT = "webapp";
    public RequestHandler(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {

        // 새로운 클라이언트 연결 시 해당 클라이언트 IP주소, port 번호 로그에 기록
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());

        // InputStream, OutputStream 사용
        // 클라이언트로부터 데이터를 읽고 응답 작성
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()){
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            // request의 첫번째 줄을 읽음
            String requestLine = br.readLine();

            if (requestLine != null) {

                // request의 path 추출
                String[] path = requestLine.split(" ");
                String method = path[0];
                String requestedFile = path[1];

                // .css 파일 요청 처리
                if (requestedFile.endsWith(".css")) {
                    File cssFile = new File(WEB_ROOT + requestedFile);
                    if (cssFile.exists() && cssFile.isFile()) {
                        serveCssFile(cssFile, dos);
                        return;
                    }
                }
                // html 파일 처리
                File file = new File(WEB_ROOT + requestedFile);
                if (file.exists() && file.isFile()) {
                    byte[] body = Files.readAllBytes(file.toPath());
                    response200Header(dos, body.length, "text/html");
                    responseBody(dos, body);
                }

                // root 요청 시 기본 파일 제공
                if ("/".equals(requestedFile)) {
                    serveFile("/index.html", dos);
                }

                Map<String, String> headers = getHeaders(br); // 헤더 정보 읽기

                // method가 GET일 경우
                if (method.equals("GET")) {

                    // UserList 표시
                    if ("/user/userList".equals(requestedFile)) {

                        Map<String, String> cookies = getCookies(headers); // 쿠키 추출
                        boolean isLoggedIn = "true".equals(cookies.get("logined"));
                        log.log(Level.INFO, "Is user logged in? " + isLoggedIn);

                        if (isLoggedIn) {
                            serveFile("/user/list.html", dos);
                        } else {
                            // 로그인되지 않은 상태이면 로그인 페이지로 리디렉션
                            dos.writeBytes("HTTP/1.1 302 Found\r\n");
                            dos.writeBytes("Location: /user/login.html\r\n");
                            dos.writeBytes("\r\n");
                        }
                    }
                }
                //method가 POST일 경우
                else if (method.equals("POST")) {

                    // Content-Length 확인 후 body 데이터 읽기
                    int contentLength = getContentLength(headers); // Content-Length 추출
                    String requestBody = IOUtils.readData(br, contentLength);  // body 데이터 읽기

                    // handleSignUp 메서드 호출
                    if ("/user/signup".equals(requestedFile)) {
                        handleSignUp(requestBody, dos);
                    }

                    // handleLogIn 메서드 호출
                    else if ("/user/login".equals(requestedFile)) {
                        handleLongIn(requestBody, dos);
                    }

                }
            }
        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }


    // 정적 파일 제공 메서드, 서버가 클라이언트에게 정적 파일의 내용을 그대로 제공
    private void serveFile(String filePath, DataOutputStream dos) throws IOException {
        Path file = Paths.get(WEB_ROOT, filePath); // 요청된 파일의 경로 설정
        if (Files.exists(file)) { // 파일이 존재하는지 확인
            byte[] body = Files.readAllBytes(file); // 파일의 내용을 바이트 배열로 읽음
            response200Header(dos, body.length,"text/html"); // 200 OK 응답 헤더 전송
            responseBody(dos, body); // 파일 내용을 응답 본문으로 전송
        } else {
            response404Header(dos); // 파일이 없으면 404 응답 전송
        }
    }
    // CSS 파일 제공 메서드
    private void serveCssFile(File file, DataOutputStream dos) throws IOException {
        byte[] body = Files.readAllBytes(file.toPath());
        response200Header(dos, body.length, "text/css"); // Content-Type text/css로 설정
        responseBody(dos, body);
    }


    // HTTP 요청의 헤더를 읽어서 Map으로 반환하는 메서드
    private Map<String, String> getHeaders(BufferedReader br) throws IOException {
        String line;
        Map<String, String> headers = new HashMap<>();

        while (!(line = br.readLine()).isEmpty()) { // 빈 줄을 만나면 헤더가 끝났음을 의미
            String[] headerParts = line.split(": ", 2);
            if (headerParts.length == 2) {
                headers.put(headerParts[0], headerParts[1].trim());
            }
        }
        return headers;
    }

    // 헤더에서 Content-Length를 추출하는 메서드
    private int getContentLength(Map<String, String> headers) {
        return headers.containsKey("Content-Length") ? Integer.parseInt(headers.get("Content-Length")) : 0;
    }

    // 헤더에서 쿠키를 추출하는 메서드
    private Map<String, String> getCookies(Map<String, String> headers) {

        Map<String, String> cookieMap = new HashMap<>();

        if (headers.containsKey("Cookie")) {
            String[] cookies = headers.get("Cookie").split(";"); // 여러 쿠키가 세미콜론으로 구분되기 때문 (sessionId=abc123; loggedIn=true; theme=dark)
            for (String cookie : cookies) {
                String[] keyValue = cookie.trim().split("=");
                if (keyValue.length == 2) {
                    cookieMap.put(keyValue[0], keyValue[1]); // 쿠키 값 분리해서 각각 키-값 쌍으로 저장
                }
            }
        }
        return cookieMap;
    }


    // SignUp 정보 parsing 및 처리 메서드
    private void handleSignUp(String requestBody, DataOutputStream dos) throws IOException {

        // requestBody는 queryString 형식으로 전달되므로 이를 parsing
        Map<String, String> queryParams = HttpRequestUtils.parseQueryParameter(requestBody);

        String userId = queryParams.get("userId");
        String password = queryParams.get("password");
        String name = queryParams.get("name");
        String email = queryParams.get("email");

        if (userId != null && password != null && name != null && email != null) {
            MemoryUserRepository userRepository = MemoryUserRepository.getInstance();
            User newUser = new User(userId, password, name, email);
            userRepository.addUser(newUser);

            // 302 index.html 리디렉션 설정
            dos.writeBytes("HTTP/1.1 302 Found\r\n");
            dos.writeBytes("Location: /index.html\r\n");
            dos.writeBytes("\r\n");
        }
    }

    // Login 정보 parsing 및 처리 메서드
    private void handleLongIn (String requestBody, DataOutputStream dos) throws IOException {

        // requestBody에 queryString 형식으로 전달되므로 이를 parsing
        Map<String, String> queryParams = HttpRequestUtils.parseQueryParameter(requestBody);

        String userId = queryParams.get("userId");
        String password = queryParams.get("password");

        MemoryUserRepository userRepository = MemoryUserRepository.getInstance();
        User user = userRepository.findUserById(userId);

        if (user != null && user.getPassword().equals(password)) {
            // 로그인 성공: login 유지 쿠키 설정, index.html로 redirect
            System.out.println("Setting cookie for user: " + userId);
            dos.writeBytes("HTTP/1.1 302 Found\r\n");
            dos.writeBytes("Set-Cookie: logined=true; Path=/; HttpOnly\r\n");
            dos.writeBytes("Location: /index.html\r\n");
            dos.writeBytes("\r\n");
        } else {
            // 로그인 실패: 로그인 실패 페이지로 redirect
            dos.writeBytes("HTTP/1.1 302 Found\r\n");
            dos.writeBytes("Location: /user/login_failed.html\r\n");
            dos.writeBytes("\r\n");
        }
    }


    // 200 response
    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, String contentType) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: " + contentType + "\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    // 404 response
    private void response404Header(DataOutputStream dos) throws IOException {
        String body = "404 Not Found";
        dos.writeBytes("HTTP/1.1 404 Not Found\r\n");
        dos.writeBytes("Content-Type: text/html; charset=utf-8\r\n");
        dos.writeBytes("Content-Length: " + body.length() + "\r\n");
        dos.writeBytes("\r\n");
        dos.writeBytes(body);
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
