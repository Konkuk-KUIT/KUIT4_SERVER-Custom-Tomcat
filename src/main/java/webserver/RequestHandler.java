package webserver;

import db.MemoryUserRepository;
import http.util.HttpRequestUtils;
import http.util.IOUtils;
import model.User;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestHandler implements Runnable {
    MemoryUserRepository userRepository;
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    public RequestHandler(Socket connection) {
        this.connection = connection;
        userRepository = MemoryUserRepository.getInstance();

    }

    @Override
    public void run() {
        //새로운 클라이언트가 들어왔을 때 실행되는 부분이고만
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            String requestLine = br.readLine(); // 요청의 첫 번째 줄을 읽음

            log.log(Level.INFO, "Received Request: " + requestLine);
            String[] requestParts = requestLine.split(" ");
            String httpMethod = requestParts[0]; //GET POST 등등
            String requestURI = requestParts[1]; // 요청 URI

            log.log(Level.INFO, "Request URI: " + httpMethod + requestURI);

            if (Objects.equals(httpMethod, "GET")) {
                if (requestURI.contains("?")) {
                    //signup part
                    String[] query = requestURI.split("\\?");
                    String userInformation = query[1];//뒤에 부분임
                    Map<String, String> userInformationMap = HttpRequestUtils.parseQueryParameter(userInformation);
                    //todo; 디코딩 여부
                    String userId = userInformationMap.get("userId");
                    String password = userInformationMap.get("password");
                    String name = userInformationMap.get("name");
                    String email = userInformationMap.get("email");

                    log.log(Level.INFO, "UserInfo: " + userId + password + name + email);
                    User user = new User(userId, password, name, email);
                    userRepository.addUser(user);
                    response302Header(dos, "../index.html");
                }

                if (Objects.equals(requestURI, "/") || Objects.equals(requestURI, "/index.html")) {
                    //main 화면 파트
                    byte[] body = Files.readAllBytes(Paths.get("webapp", "index.html")); // index.html 파일 읽기
                    response200Header(dos, body.length);
                    responseBody(dos, body);

                } else if (Objects.equals(requestURI, "/user/form.html")) {
                    //user form 기입 화면
                    byte[] body = Files.readAllBytes(Paths.get("webapp"+ requestURI)); // index.html 파일 읽기
                    response200Header(dos, body.length);
                    responseBody(dos, body);
                } else if( Objects.equals(requestURI, "/user/login.html")) {
                    byte[] body = Files.readAllBytes(Paths.get("webapp"+ requestURI));
                    response200Header(dos, body.length);
                    responseBody(dos, body);
                }else if( Objects.equals(requestURI, "/user/login_failed.html")) {
                    byte[] body = Files.readAllBytes(Paths.get("webapp"+requestURI));
                    response200Header(dos, body.length);
                    responseBody(dos, body);
                }else if(Objects.equals(requestURI, "/user/userList")){
                    //todo: 쿠키 존재 여부 확인해서 있다면, 유저리스트 없다면 login 화면으로 리다이렉트 하기
                   String cookieHeader = null;
                    while (true) {
                        String line = br.readLine();
                        if (line.equals("")) {
                            break;
                        }
                        if (line.startsWith("Cookie:")) {
                            cookieHeader = line;
                        }
                    }
                    boolean isLogined = false;
                    if (cookieHeader != null) {
                        Map<String, String> cookies = HttpRequestUtils.parseCookies(cookieHeader.split(": ")[1]);
                        isLogined = "true".equals(cookies.get("logined"));
                    }//쿠키확인

                    if(isLogined){
                        byte[] body = Files.readAllBytes(Paths.get("webapp", "user/list.html"));
                        response200Header(dos, body.length);
                        responseBody(dos, body);

                    }else{
                        //쿠키 존재하지 않음

                        response302Header(dos,"/user/login.html");
                    }


                }else if(Objects.equals(requestURI, "/user/list.html")){
                    //todo: 쿠키 존재 여부 확인해서 있다면, 유저리스트 없다면 login 화면으로 리다이렉트 하기
                    String cookieHeader = null;
                    while (true) {
                        String line = br.readLine();
                        if (line.equals("")) {
                            break;
                        }
                        if (line.startsWith("Cookie:")) {
                            cookieHeader = line;
                        }
                    }
                    boolean isLogined = false;
                    if (cookieHeader != null) {
                        Map<String, String> cookies = HttpRequestUtils.parseCookies(cookieHeader.split(": ")[1]);
                        isLogined = "true".equals(cookies.get("logined"));
                    }//쿠키확인

                    if(isLogined){
                        //쿠키 존재
                        byte[] body = Files.readAllBytes(Paths.get("webapp", "user/list.html"));
                        response200Header(dos, body.length);
                        responseBody(dos, body);
                    }else{
                        //쿠키 존재하지 않음
                        response302Header(dos,"/user/login.html");
                    }

                } else if(requestURI.endsWith("css")){
                    byte[] body = Files.readAllBytes(Paths.get("webapp", requestURI));
                    response200CssHeader(dos, body.length);
                    responseBody(dos, body);

                }

            } else if (Objects.equals(requestURI, "/user/login")&&Objects.equals(httpMethod, "POST")) {
                int requestContentLength = 0;
                while (true) {
                    final String line = br.readLine();
                    if (line.equals("")) {
                        break;
                    }
                    // header info
                    if (line.startsWith("Content-Length")) {
                        requestContentLength = Integer.parseInt(line.split(": ")[1]);
                    }
                }
                String postDataInformation = IOUtils.readData(br, requestContentLength);
                Map<String, String> signupUserInformationMap = HttpRequestUtils.parseQueryParameter(postDataInformation);

                String userId = signupUserInformationMap.get("userId");
                String password = signupUserInformationMap.get("password");
                String name = signupUserInformationMap.get("name");
                String email = signupUserInformationMap.get("email");

                if(userRepository.findUserById(userId)==null){
                    response302Header(dos,"/user/login_failed.html");
                }else{
                    response302HeaderAddCookie(dos,"../index.html");
                }

            } else if (Objects.equals(httpMethod, "POST")) {
                log.log(Level.INFO, "UserInfo: " + requestURI);
                //여기까지 잘 와짐
                int requestContentLength = 0;
                while (true) {
                    final String line = br.readLine();
                    if (line.equals("")) {
                        break;
                    }
                    // header info
                    if (line.startsWith("Content-Length")) {
                        requestContentLength = Integer.parseInt(line.split(": ")[1]);
                    }
                }
                String postDataInformation = IOUtils.readData(br, requestContentLength);
                Map<String, String> signupUserInformationMap = HttpRequestUtils.parseQueryParameter(postDataInformation);

                String userId = signupUserInformationMap.get("userId");
                String password = signupUserInformationMap.get("password");
                String name = signupUserInformationMap.get("name");
                String email = signupUserInformationMap.get("email");

                log.log(Level.INFO, "postDataInformation: " + postDataInformation);
                User user = new User(userId, password, name, email);
                userRepository.addUser(user);

                log.log(Level.INFO, "findUser: " + userRepository.findUserById(userId));
                response302Header(dos, "../index.html");

            }

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
    private void response200CssHeader(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/css;charset=utf-8\r\n"); // CSS 응답을 위한 Content-Type
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }


    private void response302Header(DataOutputStream dos, String location) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: " + location + " \r\n");
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }
    private void response302HeaderAddCookie(DataOutputStream dos, String location) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: " + location + " \r\n");
            dos.writeBytes("Set-Cookie: logined=true\r\n");  // 여기서 쿠키 추가
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            //dos에 해당 data를 0번 위치부터 읽어서 넣음
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }
}
