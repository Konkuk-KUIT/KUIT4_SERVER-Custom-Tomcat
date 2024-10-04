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
import java.nio.file.Path;
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

    /*
    TODO br 주면 파싱하는 객체를 만들고 거기서 필요한 값 받기
    한번만 써도 되는 객체에는 static 사용 고려하기
    이 객체를 다른데에서 접근하는 곳이 있나?
    static 블럭이 뭐하는 건지 알아보기
    인코딩도 IOUtils 사용하는게 좋아
    시크릿 모드에서 테스트
    리디렉트 할때 로그인 안됐으면 로그인 페이지로 리디렉트하기
    */

    @Override
    public void run() {

        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()){
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            String requestLine = br.readLine();

            System.out.println("Request Line: " + requestLine);

            if (requestLine == null || requestLine.isEmpty()) {
                return;
            }

            String[] tokens = requestLine.split(" ");
            String method = tokens[0];  // HTTP 메서드 (GET 등)
            String resource = tokens[1];  // 요청된 리소스 (예: /, /index.html)

            switch (method) {
                case "GET":
                    handleGetRequest(resource, method, dos, br);
                    break;

                case "POST":
                    handlePostRequest(resource, method, dos, br);
                    break;

                default:
                    break;
            }

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

    private void handleGetRequest(String resource, String method, DataOutputStream dos, BufferedReader br) throws IOException {
        if(isSignupRequest(resource)){
            processSignup(resource, method, dos);
        } else if(isUserListRequest(resource)) {
            processUserListRequest(resource, dos, br);
        } else {
            processGetPageRequest(resource, dos);
        }
    }

    private void handlePostRequest(String resource, String method, DataOutputStream dos, BufferedReader br) {
        String body = getBodyInPOST(br);
        if(isSignupRequest(resource)){
            processSignup(body, method, dos);
        }
        if(isLoginRequest(resource)){
            processLogin(dos, body);
        }
    }

    private void processLogin(DataOutputStream dos, String body) {
        String[] idAndPassword = body.split("&");

        if(isUserExist(idAndPassword)){
            redirectHomeByLogin(dos);
        } else {
            redirectLoginFailed(dos);
        }
    }

    private boolean isUserExist(String[] idAndPassword) {
        String inputUserId = idAndPassword[0].split("=")[1];
        String inputUserPW = idAndPassword[1].split("=")[1];

        User UserFoundById = MemoryUserRepository.getInstance().findUserById(inputUserId);
        if(UserFoundById != null){
            return isPasswordMatch(UserFoundById, inputUserPW);
        } else {
            // id가 없을 경우
            return false;
        }
    }

    private boolean isPasswordMatch(User UserFoundById, String inputUserPW) {
        if(UserFoundById.getPassword().equals(inputUserPW)){
            //비밀번호까지 일치 - 로그인 성공
            return true;

        } else {
            //비밀번호가 틀렸을 경우
            return false;
        }
    }


    private void processGetPageRequest(String resource, DataOutputStream dos) throws IOException {
        // 기본 리소스 경로 설정
        byte[] page = getPageinWebappFolder(resource);

        response200Header(dos, page.length, isRequestingCSS(resource) ? "css" : "html");

        responseBody(dos, page);
    }

    private void processUserListRequest(String resource, DataOutputStream dos, BufferedReader br) throws IOException {
        // 클라이언트에서 유저리스트로 요청이 들어온 상황

        // 1. 로그인이 된 상태인지 확인
        if(isLoggined(br)){
            processGetPageRequest(resource,dos);
        } else {
            redirectToLogin(dos);
        }
    }

    private void redirectToLogin(DataOutputStream dos) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: http://localhost:80/user/login.html\r\n");
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void processSignup(String resource, String method, DataOutputStream dos) {
        addUserInRepository(resource, method);
        redirectToHome(dos);
    }

    private static boolean isRequestingCSS(String resource) {
        String[] splitByPeriod = resource.split("\\.");
        if(splitByPeriod.length > 1){
            return splitByPeriod[1].equals("css");
        } else {
            return false;
        }
    }

    private boolean isLoggined(BufferedReader br) {
            String line = getCookieContainingLineFromHeader(br);

            if(line != null){
                return parseLoginStatusFromCookie(line);
            } else {
                return false;
            }

    }

    private String getCookieContainingLineFromHeader(BufferedReader br) {
        // 헤더 읽기
        try {
            String lineFromBr;
            while (!(lineFromBr = br.readLine()).isEmpty()) {
                //TODO 풀리퀘 올릴때 삭제
                System.out.println("\t" + lineFromBr);

                if (lineFromBr.startsWith("Cookie")) {
                    return lineFromBr;
                }
            }
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
        return null;
    }

    private boolean parseLoginStatusFromCookie(String line) {
        try {
            return Boolean.parseBoolean(line.split("=")[1]);
        } catch (ArrayIndexOutOfBoundsException e) {
            log.log(Level.WARNING, "Invalid cookie format: " + line);
            return false;
        }
    }

    private boolean isUserListRequest(String resource) {
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

    private boolean isLoginRequest(String resource) {
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
        String queries = getLineIncludingQueries(resource, method);

        String decodedQueries = URLDecoder.decode(queries, StandardCharsets.UTF_8);

        Map<String,String> queryMap = HttpRequestUtils.parseQueryParameter(decodedQueries);

        User user = new User(queryMap.get("userId"),queryMap.get("password"),queryMap.get("name"),queryMap.get("email"));

        MemoryUserRepository.getInstance().addUser(user);
    }

    private static String getLineIncludingQueries(String resource, String method) {
        String queries;
        if (method.equals("GET")) {
            queries = resource.split("&")[1];
        } else {
            queries = resource;
        }
        return queries;
    }

    private boolean isSignupRequest(String resource) {
        return resource.split("\\?")[0].equals("/user/signup");
    }

    private static byte[] getPageinWebappFolder(String resource) throws IOException {
        byte[] page = Files.readAllBytes(convertToPath(resource));

        return page;
    }

    private static Path convertToPath(String resource) {
        if (resource.equals("/")) {
            resource = "/index.html";
        }
        if(resource.equals("/user/userList")){
            resource = "/user/list.html";
        }
        return Path.of("./webapp" + resource);
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
