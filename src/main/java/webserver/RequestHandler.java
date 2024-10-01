package webserver;

import db.MemoryUserRepository;
import http.util.HttpRequestUtils;
import http.util.IOUtils;
import model.User;

import java.io.*;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;
import java.util.NoSuchElementException;
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

            String requestLine = br.readLine();
//            for(String line = br.readLine(); !line.isEmpty() ; line = br.readLine()) {
//                System.out.println("\t" + line);
//            }

            System.out.println("Request Line: " + requestLine);

            if (requestLine == null || requestLine.isEmpty()) {
                return;
            }

            String[] tokens = requestLine.split(" ");
            String method = tokens[0];  // HTTP 메서드 (GET 등)
            String resource = tokens[1];  // 요청된 리소스 (예: /, /index.html)

            switch (method) {
                case "GET":
                    if(isSignup(resource)){
                        addUserInRepository(resource, method);
                        redirectToHome(dos);
                    } else if(isUserList(resource)) {
                        // 클라이언트에서 유저리스트로 요청이 들어온 상황

                        // 1. 로그인이 된 상태인지 확인
                        if(isLoggined(br)){
                            byte[] welcomePage = getWelcomePage("/user/list.html");
                            response200Header(dos, welcomePage.length,"html");
                            responseBody(dos, welcomePage);
                        } else {
                            redirectToHome(dos);
                        }

                    } else {
                        // 기본 리소스 경로 설정
                        byte[] welcomePage = getWelcomePage(resource);

                        if(isRequestCSS(resource)){
                            response200Header(dos, welcomePage.length,"css");
                        } else {
                            response200Header(dos, welcomePage.length,"html");
                        }


                        responseBody(dos, welcomePage);
                    }
                    break;

                case "POST":
                    if(isSignup(resource)){

                        String body = getBodyInPOST(br);
                        addUserInRepository(body, method);

                        redirectToHome(dos);
                    }
                    if(isLogin(resource)){

                        String body = getBodyInPOST(br);
                        String[] idAndPassWord = body.split("&");

                        String inputUserId = idAndPassWord[0].split("=")[1];
                        String inputUserPW = idAndPassWord[0].split("=")[1];

                        User UserFoundById = MemoryUserRepository.getInstance().findUserById(inputUserId);
                        if(UserFoundById != null){

                            if(UserFoundById.getPassword().equals(inputUserPW)){
                                //비밀번호까지 일치 - 로그인 성공
                                redirectHomeByLogin(dos);

                            } else {
                                //비밀번호가 틀렸을 경우
                                redirectLoginFailed(dos);
                            }

                        } else {
                            // id가 없을 경우
                            redirectLoginFailed(dos);
                        }
                    }
                    break;

                default:
                    break;
            }




        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

    private static boolean isRequestCSS(String resource) {
        String[] splitByPeriod = resource.split("\\.");
        if(splitByPeriod.length > 1){
            return splitByPeriod[1].equals("css");
        } else {
            return false;
        }
    }

    private boolean isLoggined(BufferedReader br) {

        boolean isLoggined = false;
        try {
            // 헤더 읽기
            String line;
            System.out.println("Header: " );
            while (!(line = br.readLine()).isEmpty()) {
                System.out.println("\t" + line);
                if (line.startsWith("Cookie")) {
                    isLoggined = Boolean.parseBoolean(line.split("=")[1]);
                }
            }

        } catch (IOException | NoSuchElementException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
        return isLoggined;
    }

    private boolean isUserList(String resource) {
        return resource.split("\\?")[0].equals("/user/userList");
    }

    private void redirectHomeByLogin(DataOutputStream dos) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: http://localhost:80/\r\n");
            dos.writeBytes("Set-Cookie: logined=true; Path=/\n\r\n");
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void redirectLoginFailed(DataOutputStream dos) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: http://localhost:80/user/login_failed.html\r\n");
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private String getBodyInPOST(BufferedReader br) {
        try {
            // 헤더 읽기
            int contentLength = 0;
            String line;
            System.out.println("Header: " );
            while (!(line = br.readLine()).isEmpty()) {
                System.out.println("\t" + line);
                if (line.startsWith("Content-Length")) {
                    contentLength = Integer.parseInt(line.split(":")[1].trim());
                }
            }

            // POST 요청의 경우, IOUtils를 사용해 바디 읽기
            if (contentLength > 0) {
                String body = IOUtils.readData(br, contentLength);  // IOUtils 활용
                System.out.println("Body: " + body);
                return body;
            } else {
                throw new NoSuchElementException("There is no Body");
            }

        } catch (IOException | NoSuchElementException e) {
            log.log(Level.SEVERE, e.getMessage());
        }

        return "";
    }

    private boolean isLogin(String resource) {
        return resource.equals("/user/login");
    }

    private void redirectToHome(DataOutputStream dos) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: http://localhost:80/\r\n");
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void addUserInRepository(String resource, String method) {
        String queries;
        if (method.equals("GET")) {
            queries = resource.split("&")[1];
        } else {
            queries = resource;
        }

        String decodedQueries = URLDecoder.decode(queries, StandardCharsets.UTF_8);
        System.out.println(decodedQueries);

        Map<String,String> queryMap = HttpRequestUtils.parseQueryParameter(decodedQueries);

        queryMap.forEach((key, value) -> {
            System.out.println(key + ": " + value);
        });

        User user = new User(queryMap.get("userId"),queryMap.get("password"),queryMap.get("name"),queryMap.get("email"));

        MemoryUserRepository.getInstance().addUser(user);

        MemoryUserRepository.getInstance().findAll().forEach(System.out::println);
    }

    private boolean isSignup(String resource) {
        return resource.split("\\?")[0].equals("/user/signup");
    }

    private static byte[] getWelcomePage(String resource) throws IOException {

        if (resource.equals("/")) {
            resource = "/index.html";
        }
        File file = new File("./webapp" + resource);
        byte[] welcomePage = Files.readAllBytes(file.toPath());

        return welcomePage;
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, String contentType) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/"+contentType+";charset=utf-8\r\n");
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
