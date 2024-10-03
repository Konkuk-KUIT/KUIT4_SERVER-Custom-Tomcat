package webserver;

import db.MemoryUserRepository;
import http.HttpRequest;
import http.RequestBody;
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
    private static final String WEBAPP_PATH = "./webapp";  // webapp 폴더 경로


    public RequestHandler(Socket connection) {
        this.connection = connection;
    }


    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()){
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);
            HttpRequest httpRequest =   HttpRequest.from(br);

            String method = httpRequest.getMethod(); // 요청 방식
            String requestedUrl = httpRequest.getUrl(); // 요청된 파일

            // 루트로 요청 시 기본 파일로 index.html 반환
            if (requestedUrl.equals("/")) {
                responseRedirect(dos, "/index.html");
            }

            //GET
            if(method.equals("GET")) {
                //요구사항 2 get 방식으로 회원가입
                String queryString = httpRequest.getQueryString();; // 요청된 파일

                // 쿼리 스트링을 파싱하여 Map으로 변환
                if (queryString != null) {
                    Map<String, String> queryParams = HttpRequestUtils.parseQueryParameter(queryString);
                    MemoryUserRepository memoryUserRepository = MemoryUserRepository.getInstance();

                    //회원가임
                    if(requestedUrl.equals("/user/signup")){
                        User user = new User(queryParams.get("userId"),queryParams.get("password"),queryParams.get("name"),queryParams.get("email"));
                        memoryUserRepository.addUser(user);
                        responseRedirect(dos, "/index.html");
                    }
                }

                //요구사항 6 사용자출력
                if(requestedUrl.equals("/user/userList")){
                    String cookie = httpRequest.getCookie(); // 요청된 파일
                    if(cookie.contains("logined=true")){
                        responseRedirect(dos, "/user/list.html");
                    }
                    else{
                        responseRedirect(dos, "/user/login.html");
                    }
                }

            }

            //POST
            if(method.equals("POST")) {

                Map<String, String> queryParams = httpRequest.getQueryParams();
                MemoryUserRepository memoryUserRepository = MemoryUserRepository.getInstance();

                    //요구사항 3 POST 방식으로 회원가입
                    if(requestedUrl.equals("/user/signup")){
                        User user = new User(queryParams.get("userId"),queryParams.get("password"),queryParams.get("name"),queryParams.get("email"));
                        memoryUserRepository.addUser(user);
                        responseRedirect(dos, "/index.html"); //요구사항 4 리다이렉트 적용
                    }

                    //요구사항 5 로그인하기
                    if (requestedUrl.equals("/user/login")) {

                            //회원가입한 유저인지 찾기
                            User findUser = memoryUserRepository.findUserById(queryParams.get("userId"));
                            if(findUser == null){
                                //회원가입안함
                                responseRedirect(dos, "/user/login_failed.html");
                                return;
                            }
                            //로그인 성공
                            if(findUser.getPassword().equals(queryParams.get("password")))
                            {
                                //쿠키 생성 및 리다이렉트
                                responseRedirectWithCookie(dos, "/index.html","logined", "true");
                                return;

                            }
                            else {
                                // 비밀번호가 틀린 경우
                                responseRedirect(dos, "/user/login_failed.html");
                                return;
                            }
                    }
                }

            // 요청된 파일 경로 처리
            String filePath = WEBAPP_PATH + requestedUrl;
            File file = new File(filePath);

            //요청된 파일이 존재하면 파일 읽기 및 응답
            //요구사항 7  CSS 출력
            if (file.exists()) {
                byte[] body = Files.readAllBytes(Paths.get(filePath));
                String contentType = Files.probeContentType(Paths.get(filePath)); // MIME 타입 추출
                response200Header(dos, body.length,contentType);
                responseBody(dos, body);
            } else {
                // 파일이 존재하지 않을 경우 404 Not Found 응답
                byte[] body = "404 Not Found".getBytes();
                String contentType = Files.probeContentType(Paths.get(filePath)); // MIME 타입 추출
                response404Header(dos, body.length,contentType);
                responseBody(dos, body);
            }

        } catch (IOException e) {
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

    private void response404Header(DataOutputStream dos, int lengthOfBodyContent, String contentType) {
        try {
            dos.writeBytes("HTTP/1.1 404 Not Found \r\n");
            dos.writeBytes("Content-Type: " + contentType + ";charset=utf-8\r\n");
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

    private void responseRedirect(DataOutputStream dos, String redirectLocation) {
        try {
            // 302 Found 상태 코드와 리다이렉션 경로 설정
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: " + redirectLocation + "\r\n");
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void responseRedirectWithCookie(DataOutputStream dos, String redirectLocation, String cookieName, String cookieValue) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found\r\n");
            dos.writeBytes("Location: " + redirectLocation + "\r\n");
            //쿠키 설정
            dos.writeBytes("Set-Cookie: " + cookieName + "=" + cookieValue + "; HttpOnly; Secure; path=/\r\n");
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }


}
