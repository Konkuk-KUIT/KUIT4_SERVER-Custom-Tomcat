package webserver;

import db.MemoryUserRepository;
import http.request.HttpRequest;
import http.response.HttpResponse;
import http.util.HttpRequestUtils;
import model.User;

import java.io.*;
import java.net.Socket;
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
            HttpRequest httpRequest = HttpRequest.from(br);
            HttpResponse httpResponse = new HttpResponse(dos);

            String method = httpRequest.getMethod(); // 요청 방식
            String requestedUrl = httpRequest.getUrl(); // 요청된 파일

            log.log(Level.INFO,requestedUrl);
            // 루트로 요청 시 기본 파일로 index.html 반환
            if (requestedUrl.equals("/")) {
                httpResponse.redirect("/index.html");
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
                        httpResponse.redirect("/index.html");
                    }
                }

                //요구사항 6 사용자출력
                if(requestedUrl.equals("/user/userList")){
                    String cookie = httpRequest.getCookie(); // 요청된 파일
                    log.log(Level.INFO,cookie+"ddddddddddddddddddddddddd");
                    if(cookie.contains("logined=true")){
                        httpResponse.redirect("/user/list.html");
                    }
                    else{
                        httpResponse.redirect("/user/login.html");
                    }
                }

                httpResponse.forward(requestedUrl);

            }

            //POST
            if(method.equals("POST")) {

                Map<String, String> queryParams = httpRequest.getQueryParams();
                MemoryUserRepository memoryUserRepository = MemoryUserRepository.getInstance();

                    //요구사항 3 POST 방식으로 회원가입
                    if(requestedUrl.equals("/user/signup")){
                        User user = new User(queryParams.get("userId"),queryParams.get("password"),queryParams.get("name"),queryParams.get("email"));
                        memoryUserRepository.addUser(user);
                        httpResponse.redirect("/index.html"); //요구사항 4 리다이렉트 적용
                    }

                    //요구사항 5 로그인하기
                    if (requestedUrl.equals("/user/login")) {

                            //회원가입한 유저인지 찾기
                            User findUser = memoryUserRepository.findUserById(queryParams.get("userId"));
                            if(findUser == null){
                                //회원가입안함
                                httpResponse.redirect("/user/login_failed.html");
                                return;
                            }
                            //로그인 성공
                            if(findUser.getPassword().equals(queryParams.get("password")))
                            {
                                //쿠키 생성 및 리다이렉트
                                httpResponse.redirectWithCookie("/index.html");
                                return;

                            }
                            else {
                                // 비밀번호가 틀린 경우
                                httpResponse.redirect("/user/login_failed.html");
                                return;
                            }
                    }
                }


        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }



}
