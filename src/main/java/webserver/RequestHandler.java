package webserver;

import db.MemoryUserRepository;
import http.util.IOUtils;
import model.User;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

            byte[] body = null;

            String startLine = getStartLine(br);
            String reqURL = getRequestURL(startLine);

            String reqBody = getRequestBody(br);

            System.out.println(reqBody);        // 왜 잘 프린트 됐지?


            if (isGET(startLine)) {
                if (reqURL.equals("/") || reqURL.equals("/index.html") ) {
                    body = Files.readAllBytes(Paths.get("webapp/index.html"));
                }

                if (reqURL.equals("/user/form.html")) {
                    body = Files.readAllBytes(Paths.get("webapp/user/form.html"));
                }

                if (reqURL.startsWith("/user/signup")) {
                    System.out.println("start sign up");
                    String[] userInfo = getValueOfURLQuery(reqURL);

                    addNewUser(userInfo, dos);
                    response302Redirect(dos, "/index.html");

                    body = Files.readAllBytes(Paths.get("webapp/index.html"));
                }

                if (reqURL.equals("/user/login.html")) {
                    body = Files.readAllBytes(Paths.get("webapp/user/login.html"));    // 상대 경로 이용
                }
            }


            if (isPOST(startLine)) {
                if (reqURL.startsWith("/user/signup")) {
                    System.out.println("start sign up");
                    String[] userInfo = getValueOfQuery(reqBody);

                    addNewUser(userInfo, dos);
                    response302Redirect(dos, "/index.html");

                    body = Files.readAllBytes(Paths.get("webapp/index.html"));
                }

                if (reqURL.equals("/user/login")) {
                    String[] userInfo = getValueOfQuery(reqBody);

                    body = Files.readAllBytes(Paths.get("webapp/user/index.html"));
                    if (MemoryUserRepository.getInstance().findUserById(userInfo[0]) == null) {
                        body = Files.readAllBytes(Paths.get("webapp/user/login_failed.html"));
                    }
                }

            }





            response200Header(dos, body.length);
            responseBody(dos, body);

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

    private void addNewUser(String[] userInfo, DataOutputStream dos) {
        User user = newUser(userInfo);
        MemoryUserRepository.getInstance().addUser(user);
    }

    private static User newUser(String[] userInfo) {
        return new User(userInfo[0], userInfo[1], userInfo[2], userInfo[3]);
    }

    private boolean isGET(String startLine) {
        return startLine.startsWith("GET");
    }

    private boolean isPOST(String startLine) {
        return startLine.startsWith("POST");
    }

    private boolean isPUT(String startLine) {
        return startLine.startsWith("PUT");
    }

    private boolean isDELETE(String startLine) {
        return startLine.startsWith("DELETE");
    }

    private String getStartLine(BufferedReader br) throws IOException {
        return br.readLine();
    }

    private String getRequestURL(String startLine) {
        if (startLine != null && startLine.startsWith("GET")) {
            String[] tokens = startLine.split(" ");
            if (tokens.length > 1) {
                return tokens[1]; // 요청 URL을 반환
            }
        }
        return null;
    }

    private String getRequestBody(BufferedReader br) throws IOException {
        int requestContentLength = 0;

        while (true) {
            final String line;
            line = br.readLine();
            if (line.equals("")) { break; }

            // header info
            if (line.startsWith("Content-Length")) {
                requestContentLength = Integer.parseInt(line.split(": ")[1]);
            }
        }

        String body = IOUtils.readData(br, requestContentLength);

        return body;
    }

    private String[] getValueOfURLQuery(String urlQuery) {
        String[] buf = urlQuery.split("\\?");
        String queryString = buf[1];

        return getValueOfQuery(queryString);
    }

    private String[] getValueOfQuery(String query) {
        System.out.println("method started");
        String[] keyAndValue = query.split("&");
        System.out.println("split with &");

        String[] value = new String[keyAndValue.length];
        System.out.println("new String[]");

        for (int i = 0; i < keyAndValue.length; i++) {
            value[i] = keyAndValue[i].split("=")[1];
            System.out.println(value[i]);
        }

        return value;
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

    private void response302Redirect(DataOutputStream dos, String redirectPath){
        try {
            dos.writeBytes("HTTP/1.1 302 Found\r\n");
            dos.writeBytes("Location: " + redirectPath + "\r\n");
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
