package webserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

// 서버의 메인 실행파일 -> 클라이언트의 요청을 받는 역할
// '서버소켓 생성 + 클라이언트의 연결 대기'
// (클라이언트 <-> 서버)연결 시 RequestHandler (작업처리 스레드)실행, 각 요청 독립적으로 처리

public class WebServer {

    // todo enum으로 변환필요
    private static final int DEFAULT_PORT = 80;         // HTTP 기본포트
    private static final int DEFAULT_THREAD_NUM = 50;   // 기본 스레드 수
    private static final Logger log = Logger.getLogger(WebServer.class.getName());
    // Logger : 서버의 실행상태 / 오류정보 출력할 때 사용

    public static void main(String[] args) throws IOException {
        int port = DEFAULT_PORT;
        ExecutorService service = Executors.newFixedThreadPool(DEFAULT_THREAD_NUM);

        if (args.length != 0) {
            port = Integer.parseInt(args[0]);
        }

        // ServerSocket : TCP 환영 소켓, 특정 포트에서 클라이언트의 연결 대기

        try (ServerSocket welcomeSocket = new ServerSocket(port)){
            log.info("Webserver started on port " + port);
            // 연결 소켓
            // Socket : 클라이언트와의 개별적 연결
            Socket connection;
            // accept : (클라이언트 연결시) 새로운 Socket객체 반환
            while ((connection = welcomeSocket.accept()) != null) {

                // 스레드에 작업 전달
                // 클라이언트 요청을 개별 스레드가 처리
                service.submit(new RequestHandler(connection));
            }
        }

    }
}
