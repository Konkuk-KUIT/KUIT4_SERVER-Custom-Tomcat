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

            // 요청된 파일 경로 추출 (예: /index.html)
            String[] requestLines = requestLine.split(" ");
            String method = requestLines[0]; // 요청 방식
            String requestedFile = requestLines[1]; // 요청된 파일

            // 루트로 요청 시 기본 파일로 index.html 반환
            if (requestedFile.equals("/")) {
                responseRedirect(dos, "/index.html");
               // requestedFile = "/index.html";
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
                //String contentType = Files.probeContentType(Paths.get(filePath)); // MIME 타입 추출
                response404Header(dos, body.length,"text/html");
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
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }


}
