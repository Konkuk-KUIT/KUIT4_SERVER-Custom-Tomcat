package webserver;

import java.io.*;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Response {
    private static final String WEB_ROOT = "webapp";
    private final DataOutputStream dos;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    // OutputStream을 받아 DataOutputStream으로 초기화
    public Response(OutputStream out) {
        this.dos = new DataOutputStream(out);
    }

    // HTML 파일을 전송하는 forward 메서드
    public void forward(String path) throws IOException {
        File file = new File(WEB_ROOT + path);

        byte[] fileContent = Files.readAllBytes(file.toPath());

        // 200 OK 헤더 전송
        response200Header(dos, path, fileContent.length);
        responseBody(dos, fileContent);

    }

    // POST 요청을 처리한 후, 클라이언트를 /index.html 로 리다이렉트
    public void redirect(String path) throws IOException {
        // 302 상태 코드와 Location 헤더 설정
        log.log(Level.INFO, path);
        dos.writeBytes( HttpHeader.HTTP_302.getHeaderValue() + "\r\n");
        dos.writeBytes(HttpHeader.LOCATION.getHeaderValue() + ": "+ path + "\r\n");
        dos.writeBytes(HttpHeader.CONTENT_LENGTH.getHeaderValue() + ": 0\r\n");
        dos.writeBytes("Connection: close\r\n");
        dos.writeBytes("\r\n");
    }

    private void response200Header(DataOutputStream dos, String path, int lengthOfBodyContent) {
        try {
            String contentType = "text/html";
            if (path.endsWith(".css")) {
                contentType = "text/css";
            }
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes(HttpHeader.CONTENT_TYPE.getHeaderValue() + ": " + contentType + ";charset=utf-8\r\n");
            dos.writeBytes(HttpHeader.CONTENT_LENGTH.getHeaderValue() + ": " + lengthOfBodyContent + "\r\n");
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

    public void addCookie(String cookie) throws IOException {
        dos.writeBytes( HttpHeader.HTTP_302.getHeaderValue() + "\r\n");
        dos.writeBytes("Set-Cookie: " + cookie + "; Path=/; HttpOnly\r\n");
    }

}
