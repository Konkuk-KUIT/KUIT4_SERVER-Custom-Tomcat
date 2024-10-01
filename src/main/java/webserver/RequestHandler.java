package webserver;

import controller.*;
import db.MemoryUserRepository;
import db.Repository;
import enums.route.PageRoute;
import http.HttpRequest;
import http.HttpResponse;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import static enums.http.HttpMethod.GET;
import static enums.extension.FileExtension.*;

public class RequestHandler implements Runnable{
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    private final Repository repository;
    private Controller controller = new ForwardController();

    public RequestHandler(Socket connection) {
        this.connection = connection;
        repository = MemoryUserRepository.getInstance();
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()){
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            HttpRequest httpRequest = HttpRequest.from(br);
            HttpResponse httpResponse = new HttpResponse(dos);

            String requestMethod = httpRequest.getHttpMethod();
            String requestUrl = httpRequest.getUrl();

            // 요구 사항 1번
            if (requestMethod.equals(GET.getValue()) && requestUrl.endsWith(HTML.addFrontPoint())) {
                controller = new ForwardController();
            }

            // 요구 사항 7번
            if(requestMethod.equals(GET.getValue()) && requestUrl.endsWith(CSS.addFrontPoint())) {
                controller = new ForwardController();
            }

            if (requestUrl.equals(PageRoute.HOME.getRoute())) {
                controller = new HomeController();
            }

            // 요구 사항 2,3,4번
            if (requestUrl.equals(PageRoute.SIGNUP.getRoute())) {
                controller = new SignUpController(repository);
            }

            // 요구 사항 5번
            if (requestUrl.equals(PageRoute.LOGIN.getRoute())) {
                controller = new LoginController(repository);
            }

            // 요구 사항 6번
            if (requestUrl.equals(PageRoute.USER_LIST.getRoute())) {
                controller = new ListController();
            }

            controller.execute(httpRequest, httpResponse);

        } catch (IOException e) {
            // 예외 처리할 때 400 응답 뱉어야 될 것 같은데.. 헷갈리네
            log.log(Level.SEVERE,e.getMessage());
        }
    }
}
