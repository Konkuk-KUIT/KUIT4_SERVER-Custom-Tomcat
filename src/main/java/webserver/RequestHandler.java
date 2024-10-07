package webserver;

import webserver.Controller.Controller;
import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestHandler implements Runnable {
    private final Socket connection; // 클라이언트 연결 소켓
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    public RequestHandler(Socket connection) {
        this.connection = connection; // 소켓 초기화
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            HttpRequest request = new HttpRequest(br);
            HttpResponse response = new HttpResponse(out);

            //client의 request 전체 출력
            //log.info("Complete HTTP Request:\n" + request.toString());

            Controller controller = RequestMapper.getController(request);
            controller.execute(request, response);

        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
    }
}
