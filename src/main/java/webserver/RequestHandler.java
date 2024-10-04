package webserver;

import constants.HttpHeader;
import constants.HttpMethod;
import constants.StatusCode;
import controller.*;
import http.HttpRequest;
import http.HttpResponse;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestHandler implements Runnable {
    private final Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());
    private final RequestMapper requestMapper;

    public RequestHandler(Socket connection) {
        this.connection = connection;
        this.requestMapper = new RequestMapper(); // RequestMapper 초기화
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            // Request와 Response 객체 생성
            HttpRequest httpRequest = HttpRequest.from(br);
            HttpResponse httpResponse = new HttpResponse("HTTP/1.1", out);

            String url = httpRequest.getPath();
            HttpMethod method = httpRequest.getMethod();

            // GET 요청으로 HTML 파일을 요청하는 경우 ForwardController 실행
            if (method == HttpMethod.GET && url.endsWith(".html")) {
                requestMapper.getController("/index.html").execute(httpRequest, httpResponse);
            } else {
                // URL에 매핑된 Controller 실행
                Controller controller = requestMapper.getController(url);

                if (controller != null) {
                    controller.execute(httpRequest, httpResponse);
                } else {
                    // 매핑된 컨트롤러가 없으면 404 반환
                    httpResponse.setStatusCode(StatusCode.NOT_FOUND);
                    httpResponse.setBody("404 Not Found".getBytes());
                    httpResponse.addHeader(HttpHeader.CONTENT_TYPE, "text/html");
                    httpResponse.forward();
                }
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        }
    }
}