package webserver;

import db.MemoryUserRepository;
import db.Repository;
import model.User;
import webserver.controller.*;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestHandler implements Runnable{
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());
    // 여기서 repository DI 받음
    public RequestHandler(Socket connection) {
        this.connection = connection;
    }


    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()){
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);
            Controller forwardController = new ForwardController();
            // HttpRequest 객체 생성
            HttpRequest httpRequest = HttpRequest.from(br);
            HttpResponse httpResponse = new HttpResponse(dos);

            RequestMapper requestMapper = new RequestMapper(httpRequest, httpResponse);
            requestMapper.proceed();

            } catch (IOException e) {
                log.log(Level.SEVERE, e.getMessage(), e);
            }
        }



            // 요청 정보 확인
//            String method = httpRequest.getMethod();
//            String path = httpRequest.getPath();
//            String cookieHeader = httpRequest.getHeaders().get("Cookie");
//
//            HashMap<String, String> queryParams = new HashMap<>();
//            HashMap<String, String> bodyParams = new HashMap<>();
//
//            queryParams = (HashMap<String, String>) httpRequest.getQueryParams();
//            bodyParams = (HashMap<String, String>) httpRequest.getBodyParams();


//            if (method.equals(HttpMethod.GET.getMethod()) && path.equals(URLPath.SIGNUP.getPath())) {
//                // 요구사항2 : get 요청으로 회원가입할때
//                String userId = queryParams.get(UserQueryKey.USER_ID.getKey());
//                String password = queryParams.get(UserQueryKey.PASSWORD.getKey());
//                String name = queryParams.get(UserQueryKey.NAME.getKey());
//                String email = queryParams.get(UserQueryKey.EMAIL.getKey());
//                User user = new User(userId, password, name, email);
//                if (userRepository.findUserById(userId) == null){
//                    userRepository.addUser(user);
//                }
//                httpResponse.redirect(URLPath.INDEX.getPath());
//            } else if (method.equals(HttpMethod.POST.getMethod()) && path.equals(URLPath.SIGNUP.getPath())) {
//                // 요구사항3 : POST 요청으로 회원가입할때
//                String userId = bodyParams.get(UserQueryKey.USER_ID.getKey());
//                String password = bodyParams.get(UserQueryKey.PASSWORD.getKey());
//                String name = bodyParams.get(UserQueryKey.NAME.getKey());
//                String email = bodyParams.get(UserQueryKey.EMAIL.getKey());
//                User user = new User(userId, password, name, email);
//                if (userRepository.findUserById(userId) == null){
//                    userRepository.addUser(user);
//                }
//                httpResponse.redirect(URLPath.INDEX.getPath());
//            } else if (method.equals(HttpMethod.POST.getMethod()) && path.equals("/user/login")) {
                // 요구사항5 : 로그인 요청
//                String userId = bodyParams.get("userId");
//                String password = bodyParams.get("password");
//                User user = userRepository.findUserById(userId);
//                if (user != null){
//                    if (password.equals(user.getPassword())){
//                        //todo 아직 쿠키 res에 대해선 리펙토링 안함.
//                        response302RedirectWithCookie(dos, URLPath.INDEX.getPath(),"logined=true");
//                    }
//                }
//                httpResponse.redirect(URLPath.LOGINFAIL.getPath());
//            } else if (path.equals("/user/userList")) {
//                // 요구사항 6 :
//                if (cookieHeader == null || !(cookieHeader.contains("logined=true"))) {
//                    httpResponse.redirect(URLPath.INDEX.getPath());
//                }
//                httpResponse.forward(URLPath.LIST.getPath());
//            } else if (path.equals("/")){
                // 요구사항1 : / 요청으로 index.html 이동
//                httpResponse.forward(URLPath.INDEX.getPath());
            // 지정된 패쓰가 없으면 그래도 정적 파일
//            }  else {
//                httpResponse.forward(path);
//            } catch (IOException e) {
//            throw new RuntimeException(e);
//        }




//    private void response302RedirectWithCookie(DataOutputStream dos, String url, String cookie) {
//        try {
//            dos.writeBytes("HTTP/1.1 302 Found\r\n");
//            dos.writeBytes("Set-Cookie: " + cookie + "\r\n");
//            dos.writeBytes("Location: " + url + "\r\n");
//            dos.writeBytes("\r\n");
//        } catch (IOException e) {
//            log.log(Level.SEVERE, e.getMessage());
//        }
//    }
//
//    private String getContentType(String filePath) {
//        String lowerCasePath = filePath.toLowerCase();
//        if (lowerCasePath.endsWith(".html")) {
//            return "text/html;charset=utf-8";
//        } else if (lowerCasePath.endsWith(".css")) {
//            return "text/css;charset=utf-8";
//        } else if (lowerCasePath.endsWith(".js")) {
//            return "application/javascript;charset=utf-8";
//        } else {
//            return "text/plain;charset=utf-8";
//        }
//    }

}
