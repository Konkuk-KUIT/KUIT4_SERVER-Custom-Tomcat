package webserver;

import db.MemoryUserRepository;
import http.request.HttpRequest;
import http.response.HttpResponse;
import model.User;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

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

            HttpResponse httpResponse = new HttpResponse(dos, log);

            //요구사항 1
            if(httpRequest.isGetMethod() && url.endsWith(".html")){
                httpResponse.forward(url);
            }

            if(url.startsWith(SIGNUP.getUrl())){
                //요구사항 2,3
                createNewUser(httpRequest.getQueryParameters());
                httpResponse.redirect(INDEX.getUrl(), httpRequest.isLogin());
                return;
            }

            //요구사항 5
            if (url.equals(LOGIN.getUrl())) {
                login(httpRequest.getQueryParametersfromBody(), httpResponse);
                return;
            }

            //요구사항 6
            if (url.equals(USER_LIST.getUrl())) {
                if (httpRequest.isLogin()) {
                    httpResponse.redirect(LIST.getUrl(), httpRequest.isLogin());
                    return;
                }
                httpResponse.redirect(LOGIN_HTML.getUrl(), httpRequest.isLogin());
                return;
            }

            //요구사항 7
            if(httpRequest.isGetMethod() && url.endsWith(".css")){
                httpResponse.setCss(url);
                return;
            }

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

    private static User findUser(Map<String, String> loginInfo) {
        MemoryUserRepository memoryUserRepository = MemoryUserRepository.getInstance();
        return memoryUserRepository.findUserById(loginInfo.get(USER_ID.getKey()));
    }

    //todo 로그인 성공한 후에 다시 로그인 창에 들어가서 로그인 실패하면??
    private void login(Map<String, String> loginInfo, HttpResponse httpResponse) throws IOException {
        User findUser = findUser(loginInfo);
        if (findUser != null && findUser.getPassword().equals(loginInfo.get(PASSWORD.getKey()))) {
            httpResponse.redirect(INDEX.getUrl(), true);
            return;
        }
        httpResponse.redirect(LOGIN_FAILED.getUrl(), false);
    }

    private void createNewUser(Map<String, String> queryParameter) {
        //새로운 User 객체 생성 후 Repository에 추가
        User newUser = new User(queryParameter.get(USER_ID.getKey()), queryParameter.get(PASSWORD.getKey()), queryParameter.get(NAME.getKey()), queryParameter.get(EMAIL.getKey()));
        MemoryUserRepository memoryUserRepository = MemoryUserRepository.getInstance();
        memoryUserRepository.addUser(newUser);
//        System.out.println("newUser_name = " + memoryUserRepository.findUserById(queryParameter.get("userId")).getName());
    }

}
