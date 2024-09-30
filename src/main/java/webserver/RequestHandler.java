package webserver;

import db.MemoryUserRepository;
import http.HttpRequest;
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
    private final MemoryUserRepository userRepository;

    public RequestHandler(Socket connection) {
        this.connection = connection;
        this.userRepository = MemoryUserRepository.getInstance();
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()){
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            HttpRequest httpRequest = HttpRequest.from(br);

            // 첫 번째 요청 라인만 읽기 (GET / HTTP/1.1)
            String requestLine = br.readLine();
            String path = null;
            String queryString = null;
            if (requestLine != null) {
                String[] requestParts = requestLine.split(" ");
                String method = requestParts[0];
                String fullPath = requestParts[1];
                int queryIndex = fullPath.indexOf('?');
                if (queryIndex != -1) {
                    path = fullPath.substring(0, queryIndex);
                    queryString = fullPath.substring(queryIndex + 1);
                } else {
                    path = fullPath;
                }
                System.out.println("Request: " + method + " " + path);

                // Content-Length 헤더와 Cookie 헤더 읽기
                int contentLength = 0;
                String cookies = "";
                String headerLine;
                while (!(headerLine = br.readLine()).isEmpty()) {
                    if (headerLine.startsWith("Content-Length")) {
                        contentLength = Integer.parseInt(headerLine.split(": ")[1]);
                    } else if (headerLine.startsWith("Cookie:")) {
                        cookies = headerLine.substring(7).trim();
                    }
                }

                // 요청 처리
                if("GET".equals(method) && "/index.html".equals(path)) {
                    String filePath = "webapp/index.html";
                    File file = new File(filePath);

                    if (file.exists()) {
                        byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
                        response200Header(dos, fileBytes.length);
                        responseBody(dos, fileBytes);
                    } else {
                        send404Response(dos);
                    }
                out.flush();
                }
                else if ("GET".equals(method) && "/user/form.html".equals(path)) {
                    String filePath = "webapp/user/form.html";
                    byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
                    response200Header(dos, fileBytes.length);
                    responseBody(dos, fileBytes);
                }
                else if ("GET".equals(method) && "/user/signup".equals(path)) {

                    if (queryString != null) {
                        Map<String, String> queryParams = HttpRequestUtils.parseQueryParameter(queryString);

                        // 로그로 회원가입 요청 정보 출력
                        log.info("회원가입 요청 수신 - Query String: " + queryString);

                        String userId = queryParams.get("userId");
                        String password = queryParams.get("password");
                        String name = queryParams.get("name");
                        String email = queryParams.get("email");

                        // 로그로 각 파라미터 정보 출력
                        log.info("회원가입 정보 - userId: " + userId + ", name: " + name + ", email: " + email);

                        // User 객체 생성 및 저장
                        User newUser = new User(userId, password, name, email);
                        userRepository.addUser(newUser);

                        // 로그로 저장된 사용자 정보 확인
                        log.info("새로운 사용자 저장됨 - userId: " + newUser.getUserId() + ", name: " + newUser.getName());

                        // 302 리다이렉트로 index.html 반환
                        response302Header(dos, "/index.html");

                    }
                }
                else if ("POST".equals(method) && "/user/signup".equals(path)) {
                    // body 읽기
                    String body = IOUtils.readData(br, contentLength);

                    // body에서 쿼리 파라미터 추출
                    Map<String, String> queryParams = HttpRequestUtils.parseQueryParameter(body);

                    // 로그로 회원가입 요청 정보 출력
                    log.info("회원가입 요청 수신 - Body: " + body);

                    String userId = queryParams.get("userId");
                    String password = queryParams.get("password");
                    String name = queryParams.get("name");
                    String email = queryParams.get("email");

                    // 로그로 각 파라미터 정보 출력
                    log.info("회원가입 정보 - userId: " + userId + ", password: " + password);

                    // User 객체 생성 및 저장
                    User newUser = new User(userId, password, name, email);
                    userRepository.addUser(newUser);

                    // 로그로 저장된 사용자 정보 확인
                    log.info("새로운 사용자 저장됨 - userId: " + newUser.getUserId() + ", password: " + newUser.getPassword());

                    // 302 리다이렉트로 index.html 반환
                    response302Header(dos, "/index.html");
                }
                else if ("GET". equals(method) && "/user/login.html".equals(path)) {
                    String filePath = "webapp/user/login.html";
                    File file = new File(filePath);
                    if (file.exists()) {
                        byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
                        response200Header(dos, fileBytes.length);
                        responseBody(dos, fileBytes);
                    } else {
                        send404Response(dos);
                    }
                }
                else if ("POST".equals(method) && "/user/login".equals(path)) {
                    // body 읽기
                    String body = IOUtils.readData(br, contentLength);

                    // body에서 쿼리 파라미터 추출
                    Map<String, String> queryParams = HttpRequestUtils.parseQueryParameter(body);

                    // 로그로 회원가입 요청 정보 출력
                    log.info("로그인 요청 수신 - Body: " + body);

                    String userId = queryParams.get("userId");
                    String password = queryParams.get("password");
                    String name = "123";  // 추가
                    String email = "123@123";  // 추가

                    // 저장소에서 User 객체 조회
                    User currentUser = userRepository.findUserById(userId);

                    if (currentUser != null && currentUser.equals(currentUser)) {
                        // 로그인 성공
                        log.info("로그인 성공 - userId: " + userId);

                        // 쿠키 설정 (헤더에 추가)
                        dos.writeBytes("HTTP/1.1 302 Found\r\n");
                        dos.writeBytes("Location: /index.html\r\n");
                        dos.writeBytes("Set-Cookie: logined=true\r\n");
                        dos.writeBytes("\r\n");
                    }
                    else {
                        // 로그인 실패
                        log.info("로그인 실패 - userId: " + userId+ ", password: " + password);

                        // 302 리다이렉트로 login_failed.html 반환
                        response302Header(dos, "/user/login_failed.html");
                    }
                }
                else if("GET".equals(method) && "/user/login_failed.html".equals(path)){
                    String filePath = "webapp/user/login_failed.html";
                    File file = new File(filePath);

                    if (file.exists()) {
                        byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
                        response200Header(dos, fileBytes.length);
                        responseBody(dos, fileBytes);
                    } else {
                        send404Response(dos);
                    }
                    out.flush();
                }
                else if ("GET".equals(method) && "/user/userList".equals(path)) {

                    // 로그인 상태 확인
                    boolean isLoggedIn = cookies.contains("logined=true");

                    System.out.println("Received request for path: " + path);

                    if (isLoggedIn) {
                        // 로그인 상태일 때 유저 리스트 페이지 제공
                        String filePath = "webapp/user/list.html";
                        File file = new File(filePath);
                        if (file.exists()) {
                            byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
                            response200Header(dos, fileBytes.length);
                            responseBody(dos, fileBytes);
                        }
                    } else {
                        // 비로그인 상태일 때 로그인 페이지로 리다이렉트
                        response302Header(dos, "/user/login.html");
                    }
                }
                else if ("GET".equals(method) && path.endsWith(".css")) {
                    // CSS 파일 처리
                    String filePath = "webapp" + path;  // 요청된 경로를 webapp 디렉토리와 결합

                    File cssFile = new File(filePath);

                    if (cssFile.exists()) {
                        byte[] cssBytes = Files.readAllBytes(Paths.get(filePath));

                        // CSS 파일에 대한 헤더 설정
                        responseCssHeader(dos, cssBytes.length);
                        responseBody(dos, cssBytes);
                    } else {
                        send404Response(dos);
                    }
                }
                else {
                    send404Response(dos);
                }
            }
        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }
    private void responseCssHeader(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/css;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }
    private void response302Header(DataOutputStream dos, String redirectUrl) throws IOException {
        dos.writeBytes("HTTP/1.1 302 Found \r\n");
        dos.writeBytes("Location: " + redirectUrl + "\r\n");
        dos.writeBytes("\r\n");
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
    private static void send404Response(DataOutputStream dos) throws IOException {
        String errorMessage = "<html><body><h1>404 Not Found</h1></body></html>";
        dos.writeBytes("HTTP/1.1 404 Not Found\r\n");
        dos.writeBytes("Content-Type: text/html\r\n");
        dos.writeBytes("Content-Length: " + errorMessage.length() + "\r\n");
        dos.writeBytes("\r\n");
        dos.writeBytes(errorMessage);
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
