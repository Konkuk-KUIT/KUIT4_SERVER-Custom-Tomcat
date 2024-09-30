package webserver;

import db.MemoryUserRepository;
import db.Repository;
import http.util.HttpRequestUtils;
import http.util.IOUtils;
import model.User;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestHandler implements Runnable {
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());
    private static final String WEBAPP_PATH = "webapp/";

    public RequestHandler(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);
            Repository repository = MemoryUserRepository.getInstance();

            // 요청 읽기
            String requestLine = br.readLine();
            log.log(Level.INFO, "Request Line: " + requestLine);

            // 요청 path 읽기
            String[] requestParts = requestLine.split(" ");
            String method = requestParts[0];
            String path = requestParts[1];   // 0: HTTP Method, 1: request URL, 2: HTTP Version
            log.log(Level.INFO, "METHOD: " + method);
            log.log(Level.INFO, "Request Path: " + path);

            // 요구사항 1
            //80 포트로 들어오거나, index.html로 주소가 들어올 경우 index.html을 출력하도록 함
            if (path.equals("/") || path.equals("/index.html")) {
                byte[] body = Files.readAllBytes(Paths.get(WEBAPP_PATH + "index.html"));
                response200Header(dos, body.length);
                responseBody(dos, body);
            }

            // 요구사항 2
            if (method.equals("GET") && path.equals("/user/form.html")) {
                byte[] body = Files.readAllBytes(Paths.get(WEBAPP_PATH + "/user/form.html"));
                response200Header(dos, body.length);
                responseBody(dos, body);
            }

            // 요구사항 2
            if (method.equals("GET") && path.contains("/user/signup")) {
                log.log(Level.INFO, "Signup request received GET Method");

                if (path.contains("?")) {
                    // URL 에서 쿼리 파라미터 분리
                    String queryString = subtractQueryParameters(path);
                    Map<String, String> queryParameters = HttpRequestUtils.parseQueryParameter(queryString);

                    // 파라미터들 확인
                    queryParameters.forEach((key, value) -> log.log(Level.INFO, key + "=" + value));

                    String userId = queryParameters.get("userId");
                    String password = queryParameters.get("password");
                    String name = queryParameters.get("name");
                    String email = queryParameters.get("email");

                    User user = new User(userId, password, name, email);
                    log.log(Level.INFO, "user: " + user);

                    repository.addUser(user);
                    log.log(Level.INFO, "findUser: " + repository.findUserById(userId));

                    // 요구사항 4 적용
                    response302Header(dos);
                }
            }

            // 요구사항 3
            // POST 방식으로 전송하면 쿼리 파라미터가 사라진다. 즉 parseQueryParameter를 통해서 값을 얻어올 수 없다.
            // 대신 POST 방식에서는 이 쿼리 파라미터가 body 안에 들어간다.
            if (method.equals("POST") && path.equals("/user/signup")) {
                log.log(Level.INFO, "Signup request received Post Method");

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

                String requestBody = IOUtils.readData(br, requestContentLength);
                log.log(Level.INFO, "reqeustBody: " + requestBody);

                String queryString = subtractQueryParameters(requestBody);
                Map<String, String> queryParameters = HttpRequestUtils.parseQueryParameter(queryString);

                // 파라미터들 확인
                queryParameters.forEach((key, value) -> log.log(Level.INFO, key + "=" + value));

                String userId = queryParameters.get("userId");
                String password = queryParameters.get("password");
                String name = queryParameters.get("name");
                String email = queryParameters.get("email");

                User user = new User(userId, password, name, email);
                log.log(Level.INFO, "user: " + user);

                repository.addUser(user);
                log.log(Level.INFO, "findUser: " + repository.findUserById(userId));

                // 요구사항 4 적용
                response302Header(dos);
            }

            // 요구사항 5
            if (method.equals("GET") && path.equals("/user/login.html")) {
                byte[] body = Files.readAllBytes(Paths.get(WEBAPP_PATH + "/user/login.html"));
                response200Header(dos, body.length);
                responseBody(dos, body);
            }

            // 요구사항 5
            if (method.equals("POST") && path.equals("/user/login")) {
                log.log(Level.INFO, "Login request received");

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

                String requestBody = IOUtils.readData(br, requestContentLength);
                log.log(Level.INFO, "reqeustBody: " + requestBody);

                String queryString = subtractQueryParameters(requestBody);

                Map<String, String> queryParameters = HttpRequestUtils.parseQueryParameter(queryString);

                queryParameters.forEach((key, value) -> log.log(Level.INFO, key + "=" + value));

                String userId = queryParameters.get("userId");
                String password = queryParameters.get("password");

                log.log(Level.INFO, "user: " + userId + ", password: " + password);

                User findUser = repository.findUserById(userId);

                // 입력한 ID에 대한 회원 자체가 존재하지 않는 경우
                if (findUser == null) {
                    log.log(Level.INFO, "로그인에 실패했습니다.");

                    response302LoginFailureHeader(dos);
                } else { // 입력한 ID에 대한 회원은 존재하는 경우

                    String findUserPassword = findUser.getPassword();

                    if (findUserPassword.equals(password)) {  // repository에서 찾아온 회원의 정보와 로그인 시 넘긴 회원의 정보가 일치하는 경우
                        log.log(Level.INFO, "로그인에 성공했습니다.");

                        response302LoginSuccessHeader(dos);
                    } else {  // repository에서 찾아온 회원의 정보와 로그인 시 넘긴 회원의 정보가 일치하지 않는 경우
                        log.log(Level.INFO, "로그인에 실패했습니다.");

                        response302LoginFailureHeader(dos);
                    }
                }

            }

            // 요구사항 5
            if (method.equals("GET") && path.equals("/user/login_failed.html")) {
                byte[] body = Files.readAllBytes(Paths.get(WEBAPP_PATH + "/user/login_failed.html"));
                response200Header(dos, body.length);
                responseBody(dos, body);
            }

            // 요구사항 6
            if (method.equals("GET") && path.equals("/user/userList")) {
                log.log(Level.INFO, "UserList request received GET Method");

                boolean login = false;

                while (true) {
                    final String line = br.readLine();
                    if (line.equals("")) {
                        break;
                    }

                    if (line.startsWith("Cookie")) {
                        String loginState = line.split(": ")[1];

                        if (loginState.equals("logined=true")) {
                            login = true;
                        }

                        if (loginState.equals("logined=false")) {
                            login = false;
                        }
                    }
                }

                log.log(Level.INFO, "login: " + login);

                if (login) {
                    response302UserListHeader(dos);
                } else {
                    response302LoginHeader(dos);
                }
            }

            // 요구사항 6
            if (method.equals("GET") && path.equals("/user/list.html")) {

                boolean login = false;

                while (true) {
                    final String line = br.readLine();
                    if (line.equals("")) {
                        break;
                    }

                    if (line.startsWith("Cookie")) {
                        String loginState = line.split(": ")[1];

                        if (loginState.equals("logined=true")) {
                            login = true;
                        }

                        if (loginState.equals("logined=false")) {
                            login = false;
                        }
                    }
                }

                if(login) {
                    byte[] body = Files.readAllBytes(Paths.get(WEBAPP_PATH + "/user/list.html"));
                    response200Header(dos, body.length);
                    responseBody(dos, body);
                } else{
                    response302LoginHeader(dos);
                }
            }

            // 요구사항 7
            // .css 파일 요청 처리
            if (path.endsWith(".css")) {
                byte[] body = Files.readAllBytes(Paths.get(WEBAPP_PATH + path));
                responseCssHeader(dos, body.length);  // css 응답 헤더 설정
                responseBody(dos, body);  // css 파일 내용 전송
            }



        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private String subtractQueryParameters(String path) {
        return path.substring(path.indexOf("?") + 1);
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

    // 요구사항 4
    // 그냥 index.html로 redirect
    private void response302Header(DataOutputStream dos) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: /index.html\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    // 요구사항 5
    // 로그인 성공시 index.html로 redirect
    private void response302LoginSuccessHeader(DataOutputStream dos) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: /index.html\r\n");
            dos.writeBytes("Set-Cookie: logined=true\r\n");  // 쿠키 설정
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    // 요구사항 5
    // 로그인 실패 화면으로 redirect
    private void response302LoginFailureHeader(DataOutputStream dos) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: /user/login_failed.html\r\n");
            dos.writeBytes("Set-Cookie: logined=false\r\n");  // 쿠키 설정
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    // 요구사항 6
    // user list 화면으로 redirect
    private void response302UserListHeader(DataOutputStream dos) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: /user/list.html\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    // 요구사항 6
    // 로그인 화면으로 redirect
    private void response302LoginHeader(DataOutputStream dos) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: /user/login.html\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    // 요구사항 7
    // css 화면 적용
    private void responseCssHeader(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/css;charset=utf-8\r\n");  // Content-Type을 text/css로 설정
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
