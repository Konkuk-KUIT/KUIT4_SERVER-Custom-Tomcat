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
    private static final String WEBAPP_PATH = "./webapp";  // webapp 폴더 경로


    public RequestHandler(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()){
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

//            byte[] body = "Hello World".getBytes();
//            response200Header(dos, body.length);
//            responseBody(dos, body);


            // HTTP 요청의 첫 번째 줄 읽기 (예: GET /index.html HTTP/1.1)
            String requestLine = br.readLine();
            if (requestLine == null || requestLine.isEmpty()) {
                return;
            }

            log.log(Level.INFO, "Request Line: " + requestLine);

            // 요청된 파일 경로 추출 (예: /index.html)
            String[] tokens = requestLine.split(" ");
            String method = tokens[0]; // 요청 방식
            String requestedFile = tokens[1]; // 요청된 파일

            if (requestedFile.equals("/")) {
                requestedFile = "/index.html"; // 루트로 요청 시 기본 파일로 index.html 반환
            }

            // 요청된 파일 경로 처리
            String filePath = WEBAPP_PATH + requestedFile;
            File file = new File(filePath);

            // 요청된 파일이 존재하면 파일 읽기 및 응답
            if (file.exists()) {
                byte[] body = Files.readAllBytes(Paths.get(filePath));
                String contentType = Files.probeContentType(Paths.get(filePath)); // MIME 타입 추출

                response200Header(dos, body.length,contentType);
                responseBody(dos, body);
            } else {
                // 파일이 존재하지 않을 경우 404 Not Found 응답
                byte[] body = "404 Not Found".getBytes();
                //response404Header(dos, body.length);
                responseBody(dos, body);
            }
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

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

}
