package webserver;

import db.MemoryUserRepository;
import http.util.HttpRequestUtils;
import http.util.IOUtils;
import model.User;

import java.io.*;
import java.net.Socket;
import java.net.http.HttpRequest;
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
            HttpResponse httpResponse = new HttpResponse(out);

            // HttpRequest 객체 생성
            HttpRequest2 httpRequest = HttpRequest2.from(br);
            String filePath = httpRequest.getPath();

            // HTTP 메서드 확인하기
            if (httpRequest.getMethod() == HttpMethod.POST) {
                handlePostRequest(httpResponse, filePath, httpRequest);
            } else if (httpRequest.getMethod() == HttpMethod.GET) {
                handleGetRequest(httpResponse, filePath, httpRequest);
            }
        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

    private void handlePostRequest(HttpResponse httpResponse, String filePath, HttpRequest2 httpRequest)throws IOException{
        Map<String, String> queryParms = HttpRequestUtils.parseQueryParameter(httpRequest.getBody());

        //로그인 처리
        if (filePath.startsWith(URL.USER_LOGIN.getPath())) {
            String userId = queryParms.get(UserQueryKey.USER_ID.getKey());
            String password = queryParms.get(UserQueryKey.PASSWORD.getKey());

            User user = MemoryUserRepository.getInstance().findUserById(userId);

            // 로그인 성공 확인
            if (user != null && user.getPassword().equals(password)) {
                httpResponse.redirect("/"); // 쿠키와 함께 리다이렉트
            } else {
                httpResponse.redirect("/user/login_failed.html");
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

            httpResponse.redirect("/");  // 홈으로 리다이렉트
            return;
        }
    }

    private void handleGetRequest(HttpResponse httpResponse, String filePath, HttpRequest2 httpRequest) throws IOException {
        // 유저 리스트 요청 처리
        if (filePath.startsWith(URL.USER_LIST.getPath())) {

            String cookieHeader = httpRequest.getHeader(HttpHeader.COOKIE.getHeader());

            if (cookieHeader != null && cookieHeader.contains("logined=true")) {
                httpResponse.forward("/user/list.html");
            } else {
                httpResponse.redirect("/user/login.html");
            }
            return; // 유저 리스트 요청 처리 후 종료
        }

        // CSS 파일 요청 처리
        if (filePath.endsWith(".css")) {
            httpResponse.forward("webapp" + filePath);
            return;
        }

        if ("/".equals(filePath)) {
            filePath = "/index.html";
        }
        // 파일 경로 설정
        String fullPath = "webapp" + filePath;
        File file = new File(fullPath);

        if (file.exists() && !file.isDirectory()) { // 파일이 존재하고 디렉토리가 아닌 경우
            /*byte[] body = Files.readAllBytes(Paths.get(fullPath)); // 파일 내용 읽기
            httpResponse.forward(getContentType(filePath)); // forward 메서드로 파일 내용 전송*/
            httpResponse.forward(filePath);

        } else {
            httpResponse.notFound(); // 404 Not Found 처리
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
