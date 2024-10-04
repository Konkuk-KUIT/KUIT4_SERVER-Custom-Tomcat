package webserver;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static webserver.enums.HttpHeader.*;

public class HttpResponse {
    private final DataOutputStream dos;

    public HttpResponse(OutputStream out) {
        this.dos = new DataOutputStream(out);
    }

    public void forward(String path) throws IOException {
        Path filePath = Paths.get("webapp", path);
        if (Files.exists(filePath)) {
            byte[] fileContent = Files.readAllBytes(filePath);
            response200Header(fileContent.length);
            responseBody(fileContent);
        } else {
            // 파일이 존재하지 않을 경우 404 에러 응답
            response404Header();
        }
    }

    //302 status code를 적용하기 위한 메소드
    public void redirect(String path, Boolean login) throws IOException {
        dos.writeBytes(RESPONSE_FOUND.getValue() + " \r\n");
        dos.writeBytes(LOCATION.getValue() + ": " + path + "\r\n");
        if (login) {
            dos.writeBytes(SET_COOKIE.getValue() + ": logined=true; Path=/; HttpOnly\r\n"); // 쿠키 속성 추가
        }
        dos.writeBytes(CONTENT_LENGTH.getValue() + ": 0\r\n");
        dos.writeBytes("\r\n");
        dos.flush();
    }

    private void response200Header(int lengthOfBodyContent) throws IOException {
        // 200 OK 헤더 작성
        dos.writeBytes(RESPONSE_OK.getValue() + " \r\n");
        dos.writeBytes(CONTENT_TYPE.getValue() + ": text/html;charset=utf-8\r\n"); // 콘텐츠 타입 설정
        dos.writeBytes(CONTENT_LENGTH.getValue() + ": " + lengthOfBodyContent + "\r\n"); // 본문 길이
        dos.writeBytes("\r\n");
    }

    private void responseBody(byte[] body) throws IOException {
        // 본문 전송
        dos.write(body, 0, body.length);
        dos.flush();
    }

    // 에러용으로 추가
    private void response404Header() throws IOException {
        dos.writeBytes("HTTP/1.1 404 Not Found \r\n");
        dos.writeBytes(CONTENT_TYPE.getValue() + ": text/html;charset=utf-8\r\n");
        dos.writeBytes("\r\n");
        dos.flush();
    }

    public void sendCss(String path) throws IOException {
        Path filePath = Paths.get("webapp", path);
        if (Files.exists(filePath)) {
            byte[] fileContent = Files.readAllBytes(filePath);
            dos.writeBytes(RESPONSE_OK.getValue() + " \r\n");
            dos.writeBytes(CONTENT_TYPE.getValue() + ": text/css\r\n");
            dos.writeBytes(CONTENT_LENGTH.getValue() + ": " + fileContent.length + "\r\n");
            dos.writeBytes("\r\n");
            dos.write(fileContent);
            dos.flush();
        } else {
            response404Header();
        }
    }
}