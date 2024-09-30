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

import static http.util.HttpRequestUtils.*;
import static http.util.IOUtils.*;

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

            //BufferReader 읽어들이기
//            String s = IOUtils.readData(br, 700);
//            System.out.println("s = " + s);

            //도메인 추출
            String startLine = br.readLine();
            String[] split = startLine.split(" ");
            String method = split[0];
            String url = split[1];
            System.out.println(url);
            int requestContentLength = 0;

            //로그인 성공여부
            Boolean isLogin = false;
            System.out.println("isLogin 초기화");

            //header 추출
            while (true) {
                final String line = br.readLine();
                if (line.equals("")) {
                    break;
                }
                // header info
                if (line.startsWith("Content-Length")) {
                    requestContentLength = Integer.parseInt(line.split(": ")[1]);
                }

                if (line.startsWith("Cookie") && line.contains("logined=true")) {
                    System.out.println("로그인 되어있음!!");
                    isLogin = true;
                }
            }

            //요구사항 1
            if(method.equals("GET") && url.equals("/")){
                body = Files.readAllBytes(Paths.get("./webapp/index.html"));
            }

            if(method.equals("GET") && url.endsWith(".html")){
                body = Files.readAllBytes(Paths.get("./webapp"+url));
            }

            if(url.startsWith("/user/signup")){
                //요구사항 2
                if (method.equals("GET")) {
                    //url에서 queryString 분리
                    String queryString = url.substring(url.lastIndexOf("?") + 1);
                    register(queryString);
                }
                //요구사항 3
                if (method.equals("POST")) {
                    //body에서 queryString 추출
                    String queryString = readData(br, requestContentLength);
                    register(queryString);
                }
                response302Header(dos, "/index.html", isLogin);
                return;
            }

            //요구사항 5
            if (method.equals("POST") && url.equals("/user/login")) {
                Map<String, String> loginInfo = parseQueryParameter(readData(br, requestContentLength));
                MemoryUserRepository memoryUserRepository = MemoryUserRepository.getInstance();
                User findUser = memoryUserRepository.findUserById(loginInfo.get("userId"));
                login(findUser, loginInfo, dos);
                return;
            }

            //요구사항 6
            //todo /user/list.html 로 들어오는 url은 처리하지 않아도 되나? (로그인화면 -> UserList 클릭)
            if (url.equals("/user/userList")) {
                if (isLogin) {
                    response302Header(dos, "/user/list.html", isLogin);
                    return;
                }
                response302Header(dos, "/user/login.html", isLogin);
                return;
            }

            response200Header(dos, body.length);
            responseBody(dos, body);

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

    private void login(User findUser, Map<String, String> loginInfo, DataOutputStream dos) {
        if (findUser != null && findUser.getPassword().equals(loginInfo.get("password"))) {
            response302Header(dos, "/index.html", true);
            return;
        }
        response302Header(dos, "/user/login_failed.html", false);
    }

    //queryString을 통해 새로운 User 정보를 생성하는 메서드
    private static void register(String queryString) {
        //queryString으로부터 쿼리파라미터 파싱
        Map<String, String> queryParameter = parseQueryParameter(queryString);
        createNewUser(queryParameter);
    }

    private static void createNewUser(Map<String, String> queryParameter) {
        //새로운 User 객체 생성 후 Repository에 추가
        User newUser = new User(queryParameter.get("userId"), queryParameter.get("password"), queryParameter.get("name"), queryParameter.get("email"));
        MemoryUserRepository memoryUserRepository = MemoryUserRepository.getInstance();
        memoryUserRepository.addUser(newUser);
//        System.out.println("newUser_name = " + memoryUserRepository.findUserById(queryParameter.get("userId")).getName());
    }

    //todo 요구사항 7? (안해도 이미 css 적용되어있음..)
    private void responseCssHeader(DataOutputStream dos) {
        try {
            dos.writeBytes("GET ./css/style.css HTTP/1.1 \r\n");
            dos.writeBytes("Content-Type: text/css;charset=utf-8\r\n");
            dos.writeBytes("Host: ~ \r\n");
            dos.writeBytes("Accept: text/css,*/*;q=0.1 \r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    //요구사항 4(302 status code 적용)
    private void response302Header(DataOutputStream dos, String url, boolean isLogin) {
        try {
            dos.writeBytes("HTTP/1.1 302 REDIRECT \r\n");
            dos.writeBytes("Location: "+url+"\r\n");
            if(isLogin){
                dos.writeBytes("Set-Cookie: logined=" +isLogin+ " \r\n");
            }
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
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

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

}
