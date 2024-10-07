package webserver;

import db.MemoryUserRepository;
import db.Repository;
import webserver.controller.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RequestMapper {
    private final HttpRequest httpRequest;
    private final HttpResponse httpResponse;
    private static final Map<String, Controller> controllers = new HashMap<>();

    // DI 주입!!!
    private static final Repository userRepository = MemoryUserRepository.getInstance();

    public RequestMapper(HttpRequest httpRequest, HttpResponse httpResponse) {
        this.httpRequest = httpRequest;
        this.httpResponse = httpResponse;

        // 컨트롤러 등록
        initializeControllers();
    }

    private void initializeControllers() {

        // controllers 맵에 등록
        controllers.put("/user/signup", new SignUpController());
        controllers.put("/user/login", new LoginController());
        controllers.put("/user/userList", new ListController());
        controllers.put("/", new HomeController());
    }

    public void proceed() throws IOException {
        Controller controller = controllers.get(httpRequest.getPath());
        Controller forwardController = new ForwardController();
        if (controller != null) {
            controller.execute(httpRequest, httpResponse);
        } else {
            //todo 404 Not Found 처리

            if (httpRequest.getMethod().equals("GET")) {
                forwardController.execute(httpRequest, httpResponse);
            }
        }
    }
}
