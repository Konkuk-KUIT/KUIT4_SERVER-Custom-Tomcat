package webserver;

import db.MemoryUserRepository;
import http.request.HttpRequest;
import model.User;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static http.util.HttpRequestUtils.*;
import static http.util.IOUtils.*;
import static constant.HttpMethod.*;
import static constant.URL.*;
import static constant.HttpHeader.*;
import static constant.QueryKey.*;
import static constant.HttpStatus.*;

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
            //클라이언트의 요청 메시지
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            //응답 메시지
            DataOutputStream dos = new DataOutputStream(out);
            byte[] body = "".getBytes();

            HttpRequest httpRequest = HttpRequest.from(br);
            String url = httpRequest.getUrl();

            //요구사항 1
            if(httpRequest.isGetMethod() && url.equals("/")){
                body = Files.readAllBytes(Paths.get(ROOT.getUrl()+ INDEX.getUrl()));
            }

            if(httpRequest.isGetMethod() && url.endsWith(".html")){
                body = Files.readAllBytes(Paths.get(ROOT.getUrl()+url));
            }

            if(url.startsWith(SIGNUP.getUrl())){
                //요구사항 2
                if (httpRequest.isGetMethod()) {
                    //url에서 queryString 분리
                    createNewUser(httpRequest.getQueryParametersfromUrl());
                }
                //요구사항 3
                if (httpRequest.isPostMethod()) {
                    //body에서 queryString 추출
                    createNewUser(httpRequest.getQueryParametersfromBody());
                }
                response302Header(dos, INDEX.getUrl(), httpRequest.isLogin());
                return;
            }

            //요구사항 5
            if (url.equals(LOGIN.getUrl())) {
                Map<String, String> loginInfo = httpRequest.getQueryParametersfromBody();
                MemoryUserRepository memoryUserRepository = MemoryUserRepository.getInstance();
                User findUser = memoryUserRepository.findUserById(loginInfo.get(USER_ID.getKey()));
                login(findUser, loginInfo, dos);
                return;
            }

            //요구사항 6
            //todo /user/list.html 로 들어오는 url은 처리하지 않아도 되나? (로그인화면 -> UserList 클릭)
            if (url.equals(USER_LIST.getUrl())) {
                if (httpRequest.isLogin()) {
                    System.out.println("httpRequest.isLogin() = " + httpRequest.isLogin());
                    response302Header(dos, LIST.getUrl(), httpRequest.isLogin());
                    return;
                }
                response302Header(dos, LOGIN_HTML.getUrl(), httpRequest.isLogin());
                return;
            }

            //요구사항 7
            if(httpRequest.isGetMethod() && url.endsWith(".css")){
                body = Files.readAllBytes(Paths.get(ROOT.getUrl() + url));
                responseCssHeader(dos, body.length);
                responseBody(dos, body);
                return;
            }
            response200Header(dos, body.length);
            responseBody(dos, body);

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

    private void login(User findUser, Map<String, String> loginInfo, DataOutputStream dos) {
        if (findUser != null && findUser.getPassword().equals(loginInfo.get(PASSWORD.getKey()))) {
            response302Header(dos, INDEX.getUrl(), true);
            return;
        }
        response302Header(dos, LOGIN_FAILED.getUrl(), false);
    }

    private void createNewUser(Map<String, String> queryParameter) {
        //새로운 User 객체 생성 후 Repository에 추가
        User newUser = new User(queryParameter.get(USER_ID.getKey()), queryParameter.get(PASSWORD.getKey()), queryParameter.get(NAME.getKey()), queryParameter.get(EMAIL.getKey()));
        MemoryUserRepository memoryUserRepository = MemoryUserRepository.getInstance();
        memoryUserRepository.addUser(newUser);
//        System.out.println("newUser_name = " + memoryUserRepository.findUserById(queryParameter.get("userId")).getName());
    }

    private void responseCssHeader(DataOutputStream dos, int content_length) {
        try {
            dos.writeBytes(HTTP_VERSION.getHeader()+" "+ OK.getStatus()+" \r\n");
            dos.writeBytes(CONTENT_TYPE.getHeader()+": text/css;charset=utf-8\r\n");
            dos.writeBytes(CONTENT_LENGTH.getHeader()+": " + content_length + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    //요구사항 4(302 status code 적용)
    private void response302Header(DataOutputStream dos, String url, boolean isLogin) {
        try {
            dos.writeBytes(HTTP_VERSION.getHeader()+" "+ REDIRECT.getStatus()+" \r\n");
            dos.writeBytes(LOCATION.getHeader()+": "+url+" \r\n");
            if(isLogin){
                dos.writeBytes(SET_COOKIE.getHeader()+": logined=true \r\n");
            }
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes(HTTP_VERSION.getHeader()+" "+ OK.getStatus()+" \r\n");
            dos.writeBytes(CONTENT_TYPE.getHeader()+": text/html;charset=utf-8\r\n");
            dos.writeBytes(CONTENT_LENGTH.getHeader()+": " + lengthOfBodyContent + "\r\n");
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
