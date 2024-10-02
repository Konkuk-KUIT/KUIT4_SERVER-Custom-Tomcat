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
    private static final String WEBAPP_PATH = "./webapp";  // webapp 폴더 경로


    public RequestHandler(Socket connection) {
        this.connection = connection;
    }

    //시크릿모드에서 테스트 쿠키안남음

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()){
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);



            //요구사항 1  index.html 반환하기
            // HTTP 요청의 첫 번째 줄 읽기 (예: GET /index.html HTTP/1.1)
            String requestLine = br.readLine();
            if (requestLine == null || requestLine.isEmpty()) {
                return;
                //예외터트ㅕ야되나?
            }
            log.log(Level.INFO, "Request Line: " + requestLine);

            //헤더읽기
//            String headerLine;
//            while ((headerLine = br.readLine()) != null && !headerLine.isEmpty()) {
//                log.log(Level.INFO, "Header Line: " + headerLine);
//            }
            int requestContentLength = 0;
            String cookie = "";
            while (true) {
                final String line = br.readLine();
                if (line.equals("")) {
                    break;
                }
                // 헤더 정보
                if (line.startsWith("Content-Length")) {
                    requestContentLength = Integer.parseInt(line.split(": ")[1]);
                    log.log(Level.INFO, "Content-Length " +  requestContentLength);
                }
                if (line.startsWith("Cookie")) {
                    cookie = line.split(": ")[1].trim(); // 쿠키 값을 읽고 공백 제거
                    log.log(Level.INFO, "Cookie " + cookie);
                }

            }

            // 요청된 파일 경로 추출 (예: /index.html)
            String[] requestLines = requestLine.split(" ");
            String method = requestLines[0]; // 요청 방식
            String requestedFile = requestLines[1]; // 요청된 파일

            // 루트로 요청 시 기본 파일로 index.html 반환
            if (requestedFile.equals("/")) {
                responseRedirect(dos, "/index.html");
               // requestedFile = "/index.html";
            }

            if(method.equals("GET")) {

                //요구사항 2 get 방식으로 회원가입

                //쿼리스트링 분리
                String[] pathAndQuery = requestedFile.split("\\?");
                String filePathQuery = pathAndQuery[0]; // 파일 경로 (예: /index.html)
                String queryString = pathAndQuery.length > 1 ? pathAndQuery[1] : null; // 쿼리 스트링이 있으면 추출

                // 쿼리 스트링을 파싱하여 Map으로 변환
                if (queryString != null) {
                    Map<String, String> queryParams = HttpRequestUtils.parseQueryParameter(queryString);
                    MemoryUserRepository memoryUserRepository = MemoryUserRepository.getInstance();

                    if(filePathQuery.equals("/user/signup")){
                        User user = new User(queryParams.get("userId"),queryParams.get("password"),queryParams.get("name"),queryParams.get("email"));
                        memoryUserRepository.addUser(user);
                        responseRedirect(dos, "/index.html");
                    }
                }

                log.log(Level.INFO,"filePathQuery"+filePathQuery);
                log.log(Level.INFO,"cokkkkkkkkkkir"+cookie);
                if(requestedFile.equals("/user/userList")){
                    if(cookie.contains("logined=true")){
                        responseRedirect(dos, "/user/list.html");
                        log.log(Level.INFO,"어걸리면안되는데");
                    }
                    else{
                        responseRedirect(dos, "/user/login.html");
                    }

                }



            }


            if(method.equals("POST")) {


                String requestBody = IOUtils.readData(br,requestContentLength);

                // requestBody 파싱하여 Map으로 변환
                if (requestBody != null) {
                    Map<String, String> queryParams = HttpRequestUtils.parseQueryParameter(requestBody);

                    MemoryUserRepository memoryUserRepository = MemoryUserRepository.getInstance();

                    //요구사항 3 POST 방식으로 회원가입
                    if(requestedFile.equals("/user/signup")){
                        User user = new User(queryParams.get("userId"),queryParams.get("password"),queryParams.get("name"),queryParams.get("email"));
                        memoryUserRepository.addUser(user);
                        log.log(Level.INFO, "user " +  user.getUserId());
                        log.log(Level.INFO, "잘들어갔나 " +  memoryUserRepository.findUserById(user.getUserId()).getUserId());
                        responseRedirect(dos, "/index.html"); //요구사항 4 리다이렉트 적용
                    }

                    //요구사항 5 로그인하기
                    if (requestedFile.equals("/user/login")) {

                            //회원가입한 유저인지 찾기
                            User findUser = memoryUserRepository.findUserById(queryParams.get("userId"));
                            log.log(Level.INFO, "user " +  queryParams.get("userId"));
                            if(findUser == null){
                                //회원가입안함
                                responseRedirect(dos, "/user/login_failed.html");
                                return;
                            }
                            //로그인 성공
                            if(findUser.getPassword().equals(queryParams.get("password")))
                            {
                                //쿠키생성
                                responseRedirectWithCookie(dos, "/index.html","logined", "true"); // logined=true 쿠키를 설정
                                //log.log(Level.INFO, "dosss " +  dos.toString());
                                //responseRedirect(dos, "/index.html");
                                log.log(Level.INFO, "로그인성공");
                                log.log(Level.INFO, "z쿠키"+cookie);
                                //responseRedirect(dos, "/index.html");
                                return;

                            }else {
                                // 비밀번호가 틀린 경우
                                responseRedirect(dos, "/user/login_failed.html");
                                return;
                            }

                    }


                }


            }
//
//            byte[] body = Files.readAllBytes(Paths.get(requestedFile));
//            response200Header(dos, body.length,"text/html");
//            responseBody(dos, body);

            // 요청된 파일 경로 처리
            String filePath = WEBAPP_PATH + requestedFile;
            File file = new File(filePath);

//             요청된 파일이 존재하면 파일 읽기 및 응답
            if (file.exists()) {
                byte[] body = Files.readAllBytes(Paths.get(filePath));
                String contentType = Files.probeContentType(Paths.get(filePath)); // MIME 타입 추출
                log.log(Level.INFO, "contentType " +  contentType);

                response200Header(dos, body.length,contentType);
                responseBody(dos, body);
            } else {
                // 파일이 존재하지 않을 경우 404 Not Found 응답
                byte[] body = "404 Not Found".getBytes();
                String contentType = Files.probeContentType(Paths.get(filePath)); // MIME 타입 추출
                response404Header(dos, body.length,contentType);
                responseBody(dos, body);
            }
//            if (file.exists()) {
//                byte[] body = Files.readAllBytes(Paths.get(filePath));
//                String contentType;
//
//                // 요청된 파일의 확장자에 따라 MIME 타입 설정
//                if (requestedFile.endsWith(".css")) {
//                    contentType = "text/css";
//                } else {
//                    contentType = Files.probeContentType(Paths.get(filePath)); // MIME 타입 추출
//                }
//
//                response200Header(dos, body.length, contentType);
//                responseBody(dos, body);
//            } else {
//                // 파일이 존재하지 않을 경우 404 Not Found 응답
//                byte[] body = "404 Not Found".getBytes();
//                response404Header(dos, body.length, "text/html");
//                responseBody(dos, body);
//            }
        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

//    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
//        try {
//            dos.writeBytes("HTTP/1.1 200 OK \r\n");
//            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
//            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
//            dos.writeBytes("\r\n");
//        } catch (IOException e) {
//            log.log(Level.SEVERE, e.getMessage());
//        }
//    }
    //todo 이거 구현하고 Content-Type html말고 따른거 없으면 위에 코드로 다 수정해야할듯
private void response200Header(DataOutputStream dos, int lengthOfBodyContent, String contentType) {
    try {
        dos.writeBytes("HTTP/1.1 200 OK \r\n");
        dos.writeBytes("Content-Type: " + contentType + ";charset=utf-8\r\n");
        dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
        dos.writeBytes("\r\n");
    } catch (IOException e) {
        log.log(Level.SEVERE, e.getMessage());
    }
}

    private void response404Header(DataOutputStream dos, int lengthOfBodyContent, String contentType) {
        try {
            dos.writeBytes("HTTP/1.1 404 Not Found \r\n");
            dos.writeBytes("Content-Type: " + contentType + ";charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {

        //파일이름받아서 파일찾는거
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void responseRedirect(DataOutputStream dos, String redirectLocation) {
        try {
            // 302 Found 상태 코드와 리다이렉션 경로 설정
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: " + redirectLocation + "\r\n");
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

//    private void responseSetCookie(DataOutputStream dos, String name, String value) {
//        try {
//            dos.writeBytes("Set-Cookie: " + name + "=" + value + "; HttpOnly; Secure; Path=/\r\n");
//        } catch (IOException e) {
//            log.log(Level.SEVERE, e.getMessage());
//        }
//    }

    private void responseRedirectWithCookie(DataOutputStream dos, String redirectLocation, String cookieName, String cookieValue) {
        try {
            // 302 Found 상태 코드와 리다이렉션 경로 설정
            dos.writeBytes("HTTP/1.1 302 Found\r\n");
            dos.writeBytes("Location: " + redirectLocation + "\r\n");
            // 쿠키 설정
            dos.writeBytes("Set-Cookie: " + cookieName + "=" + cookieValue + "; HttpOnly; Secure; path=/\r\n");
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }


}
