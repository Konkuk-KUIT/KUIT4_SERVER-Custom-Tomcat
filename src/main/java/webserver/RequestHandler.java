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
            String httpMethod = getHttpMethod(requestLine);
            String requestURI = getRequestURI(requestLine);


            log.log(Level.INFO, "Request URI: " + httpMethod + requestURI);

            if (Objects.equals(httpMethod, "GET")) {
                if (requestURI.contains("?")) {
                    //signup part
                    String[] query = requestURI.split("\\?");
                    String userInformation = query[1];
                    getUserInformation(userInformation);

                    response302Header(dos, "../index.html");
                }

                if (Objects.equals(requestURI, "/") || Objects.equals(requestURI, "/index.html")) {
                    // 그냥 "/" 로 들어오면 안되니까
                    requestURI="/index.html";
                    normalResponse(dos,requestURI);
                } else if (Objects.equals(requestURI, "/user/form.html")) {
                    normalResponse(dos,requestURI);
                } else if( Objects.equals(requestURI, "/user/login.html")) {
                    normalResponse(dos,requestURI);
                }else if( Objects.equals(requestURI, "/user/login_failed.html")) {
                    normalResponse(dos,requestURI);
                }else if(Objects.equals(requestURI, "/user/userList")||Objects.equals(requestURI, "/user/list.html")){

                    //todo: 왜 /user/userList가 존재하는지는 잘... 프론트 오류?
                    if(checkCookie(br)){
                        normalResponse(dos,"/user/list.html");
                    }else{
                        response302Header(dos,"/user/login.html");
                    }

                } else if(requestURI.endsWith("css")){
                    byte[] body = Files.readAllBytes(Paths.get("webapp", requestURI));
                    response200CssHeader(dos, body.length);
                    responseBody(dos, body);
                }

            } else if (Objects.equals(requestURI, "/user/login")&&Objects.equals(httpMethod, "POST")) {
                int requestContentLength = readContentLength(br);
                String postDataInformation = IOUtils.readData(br, requestContentLength);
                Map<String, String> signupUserInformationMap = HttpRequestUtils.parseQueryParameter(postDataInformation);
                String userId = signupUserInformationMap.get("userId");

                checkUserInRepository(dos,userId);

            } else if (Objects.equals(httpMethod, "POST")) {
                int requestContentLength = readContentLength(br);
                String postDataInformation = IOUtils.readData(br, requestContentLength);
                getUserInformation(postDataInformation);

                response302Header(dos, "../index.html");

            }

        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }

    }

    private int readContentLength(BufferedReader br) throws IOException {
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
        return requestContentLength;
    }

    private boolean checkCookie(BufferedReader br) throws IOException {
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
        return isLogined;

    }

    private void normalResponse(DataOutputStream dos,String requestURI) throws IOException {
        byte[] body = Files.readAllBytes(Paths.get("webapp", requestURI));
        response200Header(dos, body.length);
        responseBody(dos, body);

    }

    private void checkUserInRepository(DataOutputStream dos,String userId) {
        if(userRepository.findUserById(userId)==null){
            response302Header(dos,"/user/login_failed.html");
        }else{
            response302HeaderAddCookie(dos,"../index.html");
        }
    }

    private void getUserInformation(String userInformation) {
        Map<String, String> userInformationMap = HttpRequestUtils.parseQueryParameter(userInformation);
        String userId = userInformationMap.get("userId");
        String password = userInformationMap.get("password");
        String name = userInformationMap.get("name");
        String email = userInformationMap.get("email");

        makeNewUser(userId, password, name, email);

    }

    private void makeNewUser(String userId, String password, String name, String email) {
        User user = new User(userId, password, name, email);
        userRepository.addUser(user);
    }

    private String getRequestURI(String line) throws IOException {
        String[] requestParts=splitLine(line);
        return requestParts[1];
    }

    private String getHttpMethod(String line) throws IOException {
        String[] requestParts=splitLine(line);
        return requestParts[0];
    }
    private String[] splitLine(String requestLine){
        return requestLine.split(" ");
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
