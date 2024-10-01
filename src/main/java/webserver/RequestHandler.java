package webserver;

import db.MemoryUserRepository;
import http.util.HttpRequestUtils;
import http.util.IOUtils;
import model.User;

import javax.xml.stream.Location;
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

    public RequestHandler(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()){
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            //요청 읽음
            String requestLine = br.readLine();
            log.log(Level.INFO, "Request Line: "+requestLine);

            //POST로 로그인 처리
            if(requestLine != null && requestLine.startsWith("POST")) {
                String[] tokens = requestLine.split(" ");
                String filePath = tokens[1];

                //헤더 처리
                int requestContentLength = 0;
                while (true) {
                    final String line = br.readLine();
                    if (line.equals("")) {
                        break;
                    }
                    if (line.startsWith("Content-Length")) {
                        requestContentLength = Integer.parseInt(line.split(": ")[1]);
                    }
                }
                //요청 본문 읽기
                //쿼리파라미터로 변환해서 유저 인스턴스 생성
                String body = IOUtils.readData(br, requestContentLength);
                Map<String, String> queryParms = HttpRequestUtils.parseQueryParameter(body);

                if(filePath.startsWith("/user/login")){
                    //유저 인스턴스 생성
                    String userId = queryParms.get("userId");
                    String password = queryParms.get("password");

                    //Repository에서 찾기
                    User user = MemoryUserRepository.getInstance().findUserById(userId);

                    // 로그 추가: 유저 정보와 입력된 비밀번호 확인
                    if (user != null) {
                        log.log(Level.INFO, "User found: " + userId + ", Password: " + user.getPassword());
                    } else {
                        log.log(Level.WARNING, "User not found: " + userId);
                    }
                    log.log(Level.INFO, "Attempting to log in with password: " + password);

                    //로그인 성공 확인
                    if(user != null && user.getPassword().equals(password)){
                        responseRedirectWithCookie(dos, "/", "logined=true");
                    }else{
                        responseRedirect(dos, "/user/login_failed.html");
                    }
                    return;
                }
                // 회원가입 처리 (POST 방식)
                if (filePath.startsWith("/user/signup")) {
                    String userId = queryParms.get("userId");
                    String password = queryParms.get("password");
                    String name = queryParms.get("name");
                    String email = queryParms.get("email");

                    User newUser = new User(userId, password, name, email);

                    // 유저 저장
                    MemoryUserRepository.getInstance().addUser(newUser);

                    // 302 리다이렉트
                    responseRedirect(dos, "/"); // 홈으로 리다이렉트
                    return;
                }

            }

            //GET 처리
            if(requestLine != null && requestLine.startsWith("GET")) {
                String[] tokens = requestLine.split(" ");
                String filePath = tokens[1];

                if(filePath.startsWith("/user/signup")){
                    /*String queryString = filePath.substring(filePath.indexOf("?")+1);
                    Map<String, String> queryParms = HttpRequestUtils.parseQueryParameter(queryString);

                    //유저 인스턴스 생성
                    String userId = queryParms.get("userId");
                    String password = queryParms.get("password");
                    String name = queryParms.get("name");
                    String email = queryParms.get("email");

                    User newUser = new User(userId, password, name, email);

                    //유저 저장
                    MemoryUserRepository.getInstance().addUser(newUser);

                    //302 리다이렉트
                    responseRedirect(dos, "/");*/
                    String fullPath = "webapp/user/signup.html"; // 회원가입 폼을 반환
                    File file = new File(fullPath);
                    if (file.exists() && !file.isDirectory()) {
                        // 파일이 존재할 경우
                        byte[] body = Files.readAllBytes(Paths.get(fullPath));
                        response200Header(dos, body.length, getContentType(filePath));
                        responseBody(dos, body);
                    } else {
                        response404Header(dos);
                    }
                    return;

                }

                if("/".equals(filePath)){
                    filePath = "/index.html";
                }
                //파일 경로 ㅓㅅㄹ정
                String fullPath = "webapp" + filePath;
                File file = new File(fullPath);
                if (file.exists() && !file.isDirectory()) {
                    // 파일이 존재할 경우
                    byte[] body = Files.readAllBytes(Paths.get(fullPath));
                    response200Header(dos, body.length, getContentType(filePath));
                    responseBody(dos, body);
                } else {
                    // 파일이 존재하지 않을 경우
                    response404Header(dos);
                }
            }
        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

    //쿠키 설정
    private void responseRedirectWithCookie(DataOutputStream dos, String location, String cookie) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found\r\n");
            dos.writeBytes("Location: " + location + "\r\n");
            dos.writeBytes("Set-Cookie: " + cookie + "\r\n"); // 쿠키 설정
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }
    private void responseRedirect(DataOutputStream dos, String lacation) {
        try{
            dos.writeBytes("HTTP/1.1 302 Found\r\n");
            dos.writeBytes("Location: " + lacation + "\r\n");
            dos.writeBytes("\r\n");
        }catch (IOException e){
            log.log(Level.SEVERE, e.getMessage());
        }
    }

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

    private void response404Header(DataOutputStream dos) {
        try {
            String body = "404 Not Found";
            dos.writeBytes("HTTP/1.1 404 Not Found\r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + body.length() + "\r\n");
            dos.writeBytes("\r\n");
            dos.writeBytes(body);
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }
    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            //HTTP응답을 클라이언트에 전송
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }
    //파일 확장자로 데이터 형식 구분
    private String getContentType(String filePath) {
        if (filePath.endsWith(".html")) {
            return "text/html;charset=utf-8";
        } else if (filePath.endsWith(".css")) {
            return "text/css;charset=utf-8";
        } else if (filePath.endsWith(".js")) {
            return "application/javascript;charset=utf-8";
        } else if (filePath.endsWith(".png")) {
            return "image/png";
        } else if (filePath.endsWith(".jpg") || filePath.endsWith(".jpeg")) {
            return "image/jpeg";
        } else {
            return "application/octet-stream"; // 기본 MIME 타입
        }
    }

}
