package webserver;

import db.MemoryUserRepository;
import model.User;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

// 각 클라이언트의 요청 처리
public class RequestHandler implements Runnable{
    Socket connection;  // 각 클라이언트와 연결 담당 (요청 받고, 응답전송)

    // Logger : 로그출력을 위함
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());
    private static final String Web_Folder = "webapp";

    public RequestHandler(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        // 클라이언트 연결 시 마다, 연결된 클라이언트 IP주소 및 포트번호 출력
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());

        // In/Output Stream : Socket으로부터 입출력 스트림 가져오기
        // try with resource : InputStream ~ DataOutputStream 자동반납
        try (InputStream in = connection.getInputStream();
             OutputStream out = connection.getOutputStream();
             BufferedReader br = new BufferedReader(new InputStreamReader(in)); // 문자 단위로 읽기
             DataOutputStream dos = new DataOutputStream(out)) {                // 바이트 단위의 데이터 전송 (to client)

            // 클라이언트의 요청 처리구간
            String requestLine = br.readLine();
            // request가 아예없거나, 문자열길이가 0이면 처리 X
            if(requestLine == null || requestLine.isEmpty()){
                return;
            }

            // 어떤 요구를 받았는지 콘솔에 표시
            log.log(Level.INFO, "Request Line: " + requestLine);

            String[] requestParts = requestLine.split(" ");
            if (requestParts.length < 3) { // 올바른 요청형식 인지 확인
                return;
            }

            String method = requestParts[0];    // get, post ..etc
            String path = requestParts[1];      // request path

            // 미션1
            if ("/".equals(path)) {
                path = "/index.html";
            }

            if(HttpMethod.GET.equals(method)) {

                if (path.contains("?")) {
                    path = path.split("\\?")[0];  // query string 제외한 경로
                }

                // get요청 처리
                if("/user/signup".equals(path) && "GET".equals(method)) {
                    // 예시 message : http://localhost/user/signup?username=john&password=12345

                    // query string 추출
                    String queryString = requestParts[1].split("\\?")[1];
                    // key & value 구분
                    String[] queryPart = queryString.split("&");

                    String userId = "";
                    String password = "";
                    String name = "";
                    String email = "";

                    for (String part : queryPart) {
                        String[] keyValue = part.split("=");
                        if ("userId".equals(keyValue[0])) {
                            userId = keyValue[1];
                        } else if ("password".equals(keyValue[0])) {
                            password = keyValue[1];
                        } else if ("name".equals(keyValue[0])) {
                            name = keyValue[1];
                        } else if ("email".equals(keyValue[0])) {
                            email = keyValue[1];
                        }
                    }

                    User user = new User(userId, password, name, email);

                    MemoryUserRepository repository = MemoryUserRepository.getInstance();
                    repository.addUser(user);

                    response302Redirect(dos, "/index.html");
                }
            }

            if(HttpMethod.POST.equals(method)) {
                // POST요청 처리
                if("/user/signup".equals(path) && "POST".equals(method)) {
                    int requestContentLength = 0;

                    // 헤더를 읽고 Content-Length 값을 가져옴
                    while (true) {
                        final String line = br.readLine(); // HTTP요청의 헤더를 한줄 씩
                        if (line.equals("")) { // empty라인이 나오면 header종료
                            break;  // 헤더 끝
                        }
                        // 헤더 정보 처리
                        if (line.startsWith("Content-Length")) {
                            // 본문 길이 저장
                            requestContentLength = Integer.parseInt(line.split(": ")[1]); // 요청이 몇 글자인지
                        }
                    }

                    // requestContentLength만큼 본문 데이터 읽는 부분
                    char[] body = new char[requestContentLength]; // 정확히 요청만큼 읽어야 하므로
                    br.read(body, 0, requestContentLength); // 0 ~ requestContentLength만큼 읽어서 body에 대입
                    String bodyString = new String(body); // char to string
                    String[] queryPart = bodyString.split("&"); // 데이터 추출

                    String userId = "";
                    String password = "";
                    String name = "";
                    String email = "";

                    for (String part : queryPart) {
                        String[] keyValue = part.split("=");
                        if ("userId".equals(keyValue[0])) {
                            userId = keyValue[1];
                        }
                        else if ("password".equals(keyValue[0])) {
                            password = keyValue[1];
                        }
                        else if ("name".equals(keyValue[0])) {
                            name = keyValue[1];
                        }
                        else if ("email".equals(keyValue[0])) {
                            email = keyValue[1];
                        }
                    }

                    User user = new User(userId, password, name, email);

                    MemoryUserRepository repository = MemoryUserRepository.getInstance();
                    repository.addUser(user);

                    response302Redirect(dos, "/index.html");
                }
            }

            // 미션5 : 로그인
            if("/user/login".equals(path) && "POST".equals(method)) {
                int requestContentLength = 0;

                // 헤더를 읽고 Content-Length 값을 가져옴
                while (true) {
                    final String line = br.readLine(); // HTTP요청의 헤더를 한줄 씩
                    if (line.equals("")) { // empty라인이 나오면 header종료
                        break;  // 헤더 끝
                    }
                    // 헤더 정보 처리
                    if (line.startsWith("Content-Length")) {
                        // 본문 길이 저장
                        requestContentLength = Integer.parseInt(line.split(": ")[1]); // 요청이 몇 글자인지
                    }
                }

                char[] body = new char[requestContentLength];
                br.read(body, 0, requestContentLength);
                String bodyString = new String(body);
                String[] queryPart = bodyString.split("&");

                //
                String userId = "";
                String password = "";

                // 데이터 추출
                for (String part : queryPart) {
                    String[] keyValue = part.split("=");
                    if ("userId".equals(keyValue[0])) {
                        userId = keyValue[1];
                    }
                    else if ("password".equals(keyValue[0])) {
                        password = keyValue[1];
                    }
                }

                MemoryUserRepository repository = MemoryUserRepository.getInstance();
                User user = repository.findUserById(userId);

                if (user != null && user.getPassword().equals(password)) {
                    response302CookieRedirect(dos, "/index.html", "logined=true");
                }

                else {
                    response302Redirect(dos, "/user/login_failed.html");
                }
            }

            // 미션 6 사용자 목록 출력
            if ("/user/userList".equals(path)) {
                boolean isLogined = false;
                String headerLine;

                while(! ((headerLine = br.readLine()).isEmpty())) {
                    if(headerLine.startsWith("Cookie") && headerLine.contains("logined=true")) {
                        isLogined = true;
                    }
                }

                if(isLogined) {
                    File userListFile = new File(Web_Folder + "/user/list.html");

                    if (!userListFile.exists()) {
                        byte[] body = "404 Not Found".getBytes();
                        response404Header(dos, body.length);
                        responseBody(dos,body);
                    } else {
                        byte[] body = Files.readAllBytes(Paths.get(userListFile.getPath()));
                        response200Header(dos, body.length);
                        responseBody(dos,body);
                    }
                }
                else { // 로그인 안됐으면 리다이렉트
                    response302Redirect(dos,"/user/logined_failed.html");
                }

            }

            // 미션 7 css출력
            if(path.endsWith(".css") && method.equals("GET")) {
                File cssFile = new File(Web_Folder + "/css/styles.css");

                if (!cssFile.exists()) {
                    byte[] body = "404 Not Found".getBytes();
                    response404Header(dos, body.length);
                    responseBody(dos,body);
                } else {
                    byte[] body = Files.readAllBytes(Paths.get(cssFile.getPath()));
                    responseCss200Header(dos, body.length);
                    responseBody(dos,body);
                }
            }


            // 응답파일 경로설정
            // 서버에서 해당 파일을 가리키는 객체생성
            File file = new File(Web_Folder + path);
            if (!file.exists()) {
                byte[] body = "404 Not Found".getBytes();
                response404Header(dos, body.length);
                responseBody(dos,body);
            } else {
                byte[] body = Files.readAllBytes(Paths.get(file.getPath()));
                response200Header(dos, body.length);
                responseBody(dos,body);
            }


//            // 응답처리
//            byte[] body = "Hello World".getBytes();
//            response200Header(dos, body.length);
//            responseBody(dos, body);                // 응답 본문 (Hello World) 클라이언트에게

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }


    private void response404Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 " + HttpStatus.NOT_FOUND.getCode() + " " + HttpStatus.NOT_FOUND.getMessage() + "\r\n");
            dos.writeBytes("Content-Type: " + ContentType.HTML.getType() + "\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    // 응답헤더작성
    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 " + HttpStatus.OK.getCode() + " " + HttpStatus.OK.getMessage() + "\r\n");
            dos.writeBytes("Content-Type: " + ContentType.HTML.getType() + "\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void responseCss200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 " + HttpStatus.OK.getCode() + " " + HttpStatus.OK.getMessage() + "\r\n");
            dos.writeBytes("Content-Type: " + ContentType.CSS.getType() + "\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }

    }

    private void response302Redirect(DataOutputStream dos, String location) {
        try {
            // HTTP/1.1 200 OK 클라이언트에게 HTTP 200응답 전송
            dos.writeBytes("HTTP/1.1 " + HttpStatus.FOUND.getCode() + " " + HttpStatus.FOUND.getMessage() + "\r\n");
            // 응답의 콘텐츠 타입 (html형식  utf-8인코딩)
            dos.writeBytes("Location : " + location + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302CookieRedirect(DataOutputStream dos, String location, String cookieState) {
        try {
            // HTTP/1.1 200 OK 클라이언트에게 HTTP 200응답 전송
            dos.writeBytes("HTTP/1.1 " + HttpStatus.FOUND.getCode() + " " + HttpStatus.FOUND.getMessage() + "\r\n");
            // 응답의 콘텐츠 타입 (html형식  utf-8인코딩)
            dos.writeBytes("Location : " + location + "\r\n");
            // Path=/ : 쿠키가 해당 웹사이트 전체에서 유효
            // HttpOnly = JS코드 접근 차단 (XSS 공격으로 부터 쿠키 보호)
            dos.writeBytes("Set-Cookie : " + cookieState + ";Path=/;HttpOnly\r\n" );
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    // 응답 본문작성
    // body라는 바이트 배열 -> 클라이언트에게
    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush(); // 남아있는 데이터 모두 출력버퍼에게
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

}