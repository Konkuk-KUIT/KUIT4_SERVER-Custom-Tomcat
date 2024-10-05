package webserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class WebServer {
    private static final int DEFAULT_PORT = 80;
    private static final int DEFAULT_THREAD_NUM = 50;
    private static final Logger log = Logger.getLogger(WebServer.class.getName());

    public static void main(String[] args) throws IOException {
        int port = DEFAULT_PORT;
        //비동기 작업을 싱행 하는데에 쓰임
        //newFixedThreadPool 메소드는 고정 크기의 스레드 풀을 생성->스레드 넘버 고정
        ExecutorService service = Executors.newFixedThreadPool(DEFAULT_THREAD_NUM);

        //port 번호 설정
        if (args.length != 0) {
            port = Integer.parseInt(args[0]);
        }
        // TCP 환영 소켓
        try (ServerSocket welcomeSocket = new ServerSocket(port)){

            // 연결 소켓
            //소켓 생성해서 올때까지 대기하는 부분!
            Socket connection;
            while ((connection = welcomeSocket.accept()) != null) {
                //연결되면 req
                // 스레드에 작업 전달->즉 리퀘스트 핸들러 생성해서 스레드에 넘김
                //submit 메소드는 작업을 스레드 풀에 제출
                service.submit(new RequestHandler(connection));
            }
        }

    }
}
