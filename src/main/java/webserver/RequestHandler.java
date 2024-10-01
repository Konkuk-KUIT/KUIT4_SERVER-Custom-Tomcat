package webserver;

import db.MemoryUserRepository;
import db.Repository;
import http.constant.HttpHeader;
import http.constant.HttpMethod;
import http.constant.HttpStatus;
import http.constant.HttpURL;
import model.User;
import model.UserQueryKey;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static http.constant.HttpHeader.*;
import static http.constant.HttpMethod.*;
import static http.constant.HttpStatus.*;
import static http.constant.HttpURL.*;
import static http.util.HttpRequestUtils.parseQueryParameter;
import static http.util.IOUtils.readData;
import static model.UserQueryKey.*;

public class RequestHandler implements Runnable{
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());
    private final Repository repository;
    public RequestHandler(Socket connection) {
        this.connection = connection;
        this.repository = MemoryUserRepository.getInstance();
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()){
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            String startLine = br.readLine();
            String[] startLines = startLine.split(" ");
            String method = startLines[0];
            String targetUrl = startLines[1];
            int requestContentLength = 0;
            String cookie = "";
            byte[] body = new byte[0];
            while (true) {
                final String line = br.readLine();
                if (line.equals("")) {
                    break;
                }
                // header info
                if (line.startsWith(CONTENT_LENGTH.getHeaderName())) {
                    requestContentLength = Integer.parseInt(line.split(": ")[1]);
                }
                if (line.startsWith(COOKIE.name())) {
                    cookie = line.split(": ")[1].split(";")[0];
                }
            }

            Path targetPath = Paths.get(ROOT.getUrl() + targetUrl);
            Path indexPath = Paths.get(ROOT.getUrl() + INDEX.getUrl());
            if(method.equals(GET.getMethod()) && targetUrl.equals("/index.html")){
                body = Files.readAllBytes(indexPath);
            }

            if(method.equals(GET.getMethod()) && targetUrl.equals("/")){
                body = Files.readAllBytes(indexPath);
            }
            if (targetUrl.equals("/user/form.html")) {
                body = Files.readAllBytes(targetPath);
            }

            if (targetUrl.equals("/user/signup")) {
                String query = readData(br,requestContentLength);
                Map<String, String> queryParameter = parseQueryParameter(query);
                User user = new User(queryParameter.get(ID.getKey()), queryParameter.get(PWD.getKey()), queryParameter.get(NAME.getKey()), queryParameter.get(EMAIL.getKey()));
                repository.addUser(user);
                response302Header(dos,INDEX.getUrl());
                return;
            }

            if(targetUrl.equals("/user/login.html")){
                body = Files.readAllBytes(Paths.get(ROOT_USER.getUrl()+ LOGIN.getUrl()));
            }
            if(targetUrl.equals("/login_failed.html")){
                body = Files.readAllBytes(Paths.get(ROOT_USER.getUrl()+ LOGIN_FAILED.getUrl()));
            }

            if(targetUrl.equals("/user/login")){
                String query = readData(br,requestContentLength);
                Map<String, String> queryParameter = parseQueryParameter(query);
                User loginUser = new User(queryParameter.get(ID.getKey()), queryParameter.get(PWD.getKey()), queryParameter.get(NAME.getKey()), queryParameter.get(EMAIL.getKey()));
                User userById = repository.findUserById(loginUser.getUserId());
                if(userById != null){
                    if(userById.getPassword().equals(loginUser.getPassword())){
                        response302HeaderWithCookie(dos, INDEX.getUrl());
                    }
                }
                else{
                    response302Header(dos, HttpURL.LOGIN_FAILED.getUrl());
                }
                return;
            }
            if(targetUrl.equals("/user/userList")){
                if(!cookie.equals("logined=true")){
                    response302Header(dos,"/user/login.html");
                    return;
                }
                body = Files.readAllBytes(Paths.get(ROOT_USER.getUrl()+ USER_LIST.getUrl()));
            }

            if(method.equals(GET.getMethod()) && targetUrl.endsWith(".css")){
                body = Files.readAllBytes(targetPath);
                response200HeaderWithCss(dos, body.length);
                responseBody(dos,body);
                return;
            }
            response200Header(dos, body.length);
            responseBody(dos, body);

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String path)  {
        try {
            dos.writeBytes("HTTP/1.1 "+ REDIRECT.getStatus() + " \r\n");
            dos.writeBytes(LOCATION.getHeaderName() + ": " + path + "\r\n");
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 " + OK.getStatus() + " \r\n");
            dos.writeBytes(CONTENT_TYPE.getHeaderName() + ": text/html;charset=utf-8\r\n");
            dos.writeBytes(CONTENT_LENGTH.getHeaderName() + ": " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response200HeaderWithCss(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 " + OK.getStatus() + " \r\n");
            dos.writeBytes(CONTENT_TYPE.getHeaderName() + ": text/css;charset=utf-8\r\n");
            dos.writeBytes(CONTENT_LENGTH.getHeaderName() + ": " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
            dos.flush();
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
    private void response302HeaderWithCookie(DataOutputStream dos, String path) {
        try {
            dos.writeBytes("HTTP/1.1 "+ REDIRECT.getStatus() + " \r\n");
            dos.writeBytes(LOCATION.getHeaderName() + ": " + path + "\r\n");
            dos.writeBytes(SET_COOKIE.getHeaderName() + ": logined=true" + "\r\n");
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }


}
