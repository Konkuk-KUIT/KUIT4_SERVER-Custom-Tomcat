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

            //HTTP 메서드 확인하기
            if(requestLine != null){
                String[] tokens = requestLine.split(" ");
                HttpMethod method = HttpMethod.valueOf(tokens[0].toUpperCase());
                String filePath = tokens[1];

                // POST 방식 처리
                if (method == HttpMethod.POST) {
                    handlePostRequest(br, dos, filePath);
                }
                // GET 방식 처리
                else if (method == HttpMethod.GET) {
                    handleGetRequest(br, dos, filePath);
                }

            }
        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

    private void handlePostRequest(BufferedReader br, DataOutputStream dos, String filePath)throws IOException{
        //헤더 처리
        int requestContentLength = 0;
        String line;
        while (true) {
            line = br.readLine();
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

        if(filePath.startsWith(URL.USER_LOGIN.getPath())){
            //유저 인스턴스 생성
            String userId = queryParms.get(UserQueryKey.USER_ID.getKey());
            String password = queryParms.get(UserQueryKey.PASSWORD.getKey());

            //Repository에서 찾기
            User user = MemoryUserRepository.getInstance().findUserById(userId);

            //로그인 성공 확인
            if(user != null && user.getPassword().equals(password)){
                responseRedirectWithCookie(dos, "/", "logined=true");
            }else{
                responseRedirect(dos, "/user/login_failed.html");
            }
            return;
        }

        // 회원가입 처리 (POST 방식)
        if (filePath.startsWith(URL.USER_SIGNUP.getPath())) {
            String userId = queryParms.get(UserQueryKey.USER_ID.getKey());
            String password = queryParms.get(UserQueryKey.PASSWORD.getKey());
            String name = queryParms.get(UserQueryKey.NAME.getKey());
            String email = queryParms.get(UserQueryKey.EMAIL.getKey());

            User newUser = new User(userId, password, name, email);

            // 유저 저장
            MemoryUserRepository.getInstance().addUser(newUser);

            // 302 리다이렉트
            responseRedirect(dos, "/"); // 홈으로 리다이렉트
            return;
        }
    }

    private void handleGetRequest(BufferedReader br, DataOutputStream dos, String filePath) throws IOException {
        // 유저 리스트 요청 처리
        if (filePath.startsWith(URL.USER_LIST.getPath())) {
            String cookieHeader = null;
            String line;
            while (!(line = br.readLine()).isEmpty()) {
                if (line.startsWith(HttpHeader.COOKIE.getHeader())) {
                    cookieHeader = line.split(": ")[1];
                }
            }

            // Cookie가 존재하고 logined=true인지 확인
            if (cookieHeader != null && cookieHeader.contains("logined=true")) {
                String userListPage = "webapp/user/list.html";
                File file = new File(userListPage);
                if (file.exists() && !file.isDirectory()) {
                    byte[] body = Files.readAllBytes(Paths.get(userListPage));
                    response200Header(dos, body.length, ContentType.HTML.getType());
                    responseBody(dos, body);
                } else {
                    response404Header(dos);
                }
            } else {
                responseRedirect(dos, "/user/login.html");
            }
            return; // 유저 리스트 요청 처리 후 종료
        }

        // CSS 파일 요청 처리
        if (filePath.endsWith(".css")) {
            String fullPath = "webapp" + filePath; // 요청된 CSS 파일 경로
            File file = new File(fullPath);
            if (file.exists() && !file.isDirectory()) {
                byte[] body = Files.readAllBytes(Paths.get(fullPath));
                response200Header(dos, body.length, ContentType.CSS.getType()); // Content-Type을 text/css로 설정
                responseBody(dos, body);
            } else {
                response404Header(dos);
            }
            return;
        }

        if ("/".equals(filePath)) {
            filePath = "/index.html";
        }
        // 파일 경로 설정
        String fullPath = "webapp" + filePath;
        File file = new File(fullPath);
        if (file.exists() && !file.isDirectory()) {
            byte[] body = Files.readAllBytes(Paths.get(fullPath));
            response200Header(dos, body.length, getContentType(filePath));
            responseBody(dos, body);
        } else {
            response404Header(dos);
        }
    }

    //쿠키 설정
    private void responseRedirectWithCookie(DataOutputStream dos, String location, String cookie) {
        try {
            dos.writeBytes("HTTP/1.1 " + HttpStatus.FOUND.getCode() + " " + HttpStatus.FOUND.getMessage() + "\r\n");
            dos.writeBytes(HttpHeader.LOCATION.getHeader() + ": " + location + "\r\n");
            dos.writeBytes("Set-Cookie: " + cookie + "\r\n"); // 쿠키 설정
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }
    private void responseRedirect(DataOutputStream dos, String lacation) {
        try{
            dos.writeBytes("HTTP/1.1" + HttpStatus.FOUND.getCode() + " " + HttpStatus.FOUND.getMessage() + "\r\n");
            dos.writeBytes(HttpHeader.LOCATION.getHeader() + ": " + lacation + "\r\n");
            dos.writeBytes("\r\n");
        }catch (IOException e){
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, String contextType) {
        try {
            dos.writeBytes("HTTP/1.1 " + HttpStatus.OK.getCode() + " " + HttpStatus.OK.getMessage() + "\r\n");
            dos.writeBytes(HttpHeader.CONTENT_TYPE.getHeader()+ ": " + ContentType.HTML.getType() + "\r\n");
            dos.writeBytes(HttpHeader.CONTENT_LENGTH.getHeader()+": " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response404Header(DataOutputStream dos) {
        try {
            String body = HttpStatus.NOT_FOUND.getMessage();
            dos.writeBytes("HTTP/1.1 " + HttpStatus.NOT_FOUND.getCode() + " " + body + "\r\n");
            dos.writeBytes(HttpHeader.CONTENT_TYPE.getHeader() + ": " + ContentType.HTML.getType() + "\r\n");
            dos.writeBytes(HttpHeader.CONTENT_LENGTH.getHeader() + ": " + body.length() + "\r\n");
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
            return ContentType.HTML.getType();
        } else if (filePath.endsWith(".css")) {
            return ContentType.CSS.getType();
        } else if (filePath.endsWith(".js")) {
            return ContentType.JS.getType();
        } else if (filePath.endsWith(".png")) {
            return  ContentType.PNG.getType();
        } else if (filePath.endsWith(".jpg") || filePath.endsWith(".jpeg")) {
            return ContentType.JPEG.getType();
        } else {
            return ContentType.OCTET_STREAM.getType();// 기본 MIME 타입
        }
    }

}
