package webserver;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class HttpResponse {
    private DataOutputStream dos;
    private String version;
    private int statusCode;
    private String message;

    public HttpResponse(OutputStream out) {
        this.dos = new DataOutputStream(out);
        this.version = "HTTP/1.1"; // 기본 HTTP 버전
    }

    public void forward(String path) throws IOException {
        // HTML 파일 내용 읽기
        String body = new String(Files.readAllBytes(Paths.get("webapp" + path)));
        // 응답 헤더 작성
        response200Header(body.length());
        // 응답 본문 쓰기
        dos.writeBytes(body);
    }

    public void redirect(String location) throws IOException {
        dos.writeBytes(version + " 302 Found\r\n");
        dos.writeBytes("Location: " + location + "\r\n");
        dos.writeBytes("\r\n");
        dos.flush();
    }
    public void notFound() throws IOException {
        String body = HttpStatus.NOT_FOUND.getMessage();
        dos.writeBytes(version + " " + HttpStatus.NOT_FOUND.getCode() + " " + body + "\r\n");
        dos.writeBytes(HttpHeader.CONTENT_TYPE.getHeader() + ": " + ContentType.HTML.getType() + "\r\n");
        dos.writeBytes(HttpHeader.CONTENT_LENGTH.getHeader() + ": " + body.length() + "\r\n");
        dos.writeBytes("\r\n");
        dos.writeBytes(body);
        dos.flush();
    }


    private void response200Header(int lengthOfBodyContent) throws IOException {
        dos.writeBytes(version + " 200 OK\r\n");
        dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
        dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
        dos.writeBytes("\r\n");
    }
}
