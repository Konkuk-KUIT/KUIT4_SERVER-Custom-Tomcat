package webserver;

import db.MemoryUserRepository;
import db.Repository;
import http.util.IOUtils;
import model.User;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestHandler implements Runnable{
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());
    private final Repository repository;

    public RequestHandler(Socket connection) {
        this.connection = connection;
        repository = MemoryUserRepository.getInstance();
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()){
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

//          byte[] body = "Hello World".getBytes();
//          response200Header(dos, body.length);
//          responseBody(dos, body);

            //String url = IOUtils.readData(br, 100);
            String url = br.readLine();
            String[] strings = url.split(" ");
            System.out.println(url); // 나중에 제거

            //index.html 반환하기
            if (strings[1].equals("/") || strings[1].equals("/index.html")) {
                byte[] body = Files.readAllBytes(Paths.get("webapp/index.html"));
                response200Header(dos, body.length);
                responseBody(dos, body);
            }

            //get 방식으로 회원가입하기
            if (strings[1].equals("/user/form.html")) {
                byte[] body = Files.readAllBytes(Paths.get("webapp/user/form.html"));
                response200Header(dos, body.length);
                responseBody(dos, body);
            }

            if (strings[1].startsWith("/user/signup?")) {
                String[] signUpQueryStrings = strings[1].split("\\?");
                String[] signUpDataStrings = signUpQueryStrings[1].split("&");

                String[] userId = signUpDataStrings[0].split("=");
                String[] password = signUpDataStrings[1].split("=");
                String[] name = signUpDataStrings[2].split("=");
                String[] email = signUpDataStrings[3].split("=");

                byte[] body = Files.readAllBytes(Paths.get("webapp/index.html"));
                response302Header(dos, "/index.html");
                responseBody(dos, body);

                repository.addUser(new User(userId[1], password[1], name[1], email[1]));
            }

            //post 방식으로 회원가입하기
            if (strings[1].equals("/user/signup")) {
                int requestContentLength = 0;
                while (true) {
                    final String line = br.readLine();
                    if (line.isEmpty()) {
                        break;
                    }
                    // header info
                    if (line.startsWith("Content-Length")) {
                        requestContentLength = Integer.parseInt(line.split(": ")[1]);
                    }
                }

                String queryString = IOUtils.readData(br, requestContentLength);
                String[] signUpDataStrings = queryString.split("&");

                String[] userId = signUpDataStrings[0].split("=");
                String[] password = signUpDataStrings[1].split("=");
                String[] name = signUpDataStrings[2].split("=");
                String[] email = signUpDataStrings[3].split("=");

                response302Header(dos, "/index.html");

                repository.addUser(new User(userId[1], password[1], name[1], email[1]));
            }

            //로그인하기
            if (strings[1].equals("/user/login.html")) {
                System.out.println("login");

                byte[] body = Files.readAllBytes(Paths.get("webapp/user/login.html"));
                response200Header(dos, body.length);
                responseBody(dos, body);
            }

            if (strings[1].equals("/user/login")) {
                int requestContentLength = 0;
                while (true) {
                    final String line = br.readLine();
                    if (line.isEmpty()) {
                        break;
                    }
                    // header info
                    if (line.startsWith("Content-Length")) {
                        requestContentLength = Integer.parseInt(line.split(": ")[1]);
                    }
                }

                String queryString = IOUtils.readData(br, requestContentLength);



                String[] signUpDataStrings = queryString.split("&");

                String[] userId = signUpDataStrings[0].split("=");
                String[] password = signUpDataStrings[1].split("=");

                User user = repository.findUserById(userId[1]);
                if (user == null) {
                    System.out.println("login failed");
                    response302Header(dos, "/user/login_failed.html");
                }

                System.out.println(password[1] + " " + user.getPassword());

                if (password[1].equals(user.getPassword())) {
                    System.out.println("login successed");

                    response302Header(dos, "/index.html", "logined=true");
                }

                if (!password[1].equals(user.getPassword())) {
                    System.out.println("login failed");
                    response302Header(dos, "/user/login_failed.html");
                }
            }

            if (strings[1].equals("/user/login_failed.html")) {
                byte[] body = Files.readAllBytes(Paths.get("webapp/user/login_failed.html"));
                response200Header(dos, body.length);
                responseBody(dos, body);
            }

            //사용자 목록 출력
            if (strings[1].equals("/user/userList") || strings[1].equals("/user/list.html")) {
                boolean logined = false;

                while (true) {
                    final String line = br.readLine();
                    System.out.println(line);
                    if (line.isEmpty()) {
                        break;
                    }
                    // header info
                    if (line.startsWith("Cookie")) {
                        String[] cookieStrings = line.split(" ");
                        for (String cookie : cookieStrings) {
                            if (cookie.equals("logined=true;")) {
                                System.out.println("true");
                                logined = true;
                                break;
                            }
                        }
                    }
                }

                if (logined) {
                    byte[] body = Files.readAllBytes(Paths.get("webapp/user/list.html"));
                    response200Header(dos, body.length);
                    responseBody(dos, body);
                }

                if (!logined) {
                    response302Header(dos, "/user/login.html");
                }
            }

            //css 출력
            if (strings[1].equals("/css/styles.css")) {
                while (true) {
                    final String line = br.readLine();
                    System.out.println(line);
                    if (line.isEmpty()) {
                        break;
                    }
                    // header info
                    if (line.startsWith("Content-Length")) {
                        //requestContentLength = Integer.parseInt(line.split(": ")[1]);
                    }
                }

                byte[] body = Files.readAllBytes(Paths.get("webapp/css/styles.css"));
                response200CssHeader(dos, body.length);
                responseBody(dos, body);
            }

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
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
            dos.writeBytes("Content-Type: text/css;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String redirectUrl) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: " + redirectUrl + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String redirectUrl, String cookieString) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: " + redirectUrl + "\r\n");
            dos.writeBytes("Set-Cookie: " + cookieString + "\r\n");
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
