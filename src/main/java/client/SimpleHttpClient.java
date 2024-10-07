package client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SimpleHttpClient {
    public static void main(String[] args) {
        String hostname = "localhost";
        int port = 80;  // 서버의 포트와 일치해야 합니다

        try (Socket socket = new Socket(hostname, port)) {
            // 서버로 보낼 HTTP GET 요청 생성
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println("GET /user/form.html HTTP/1.1");
            out.flush();

            // 서버로부터 응답 읽기
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine).append("\n");
            }

            // 응답 출력
            System.out.println("서버 응답:");
            System.out.println(response.toString());

        } catch (Exception e) {
            System.out.println("클라이언트 오류: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
