package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestHandler implements Runnable{
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());
    private static final String WEBAPP_DIR = "webapp";
    public RequestHandler(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()){
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);


            byte[] body;
            // GET /index.html
            String request = br.readLine();
            if (request == null || request.isEmpty()) {
                return;
            }

            // 요청 라인 파싱
            String[] tokens = request.split(" ");

            String method = tokens[0];
            String path = tokens[1];

            // 요청된 경로에 따라 파일 경로 설정
            if (path.equals("/")) {
                path = "/index.html";
            }
            String filePath = WEBAPP_DIR + path;
            File file = new File(filePath);
            if (file.exists()) {
                body = Files.readAllBytes(Paths.get(filePath));
                response200Header(dos, body.length);
                responseBody(dos, body);
            }
//            else {
//                log.log(Level.INFO,"파일 없음!!!!!!");
//            }

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

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

}
