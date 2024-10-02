package webserver;

import db.MemoryUserRepository;
import db.Repository;
import webserver.controller.*;

import java.util.HashMap;
import java.util.Map;

public class RequestMapper {
    private final HttpRequest httpRequest;
    private final HttpResponse httpResponse;
    private final Map<String, Controller> controllers;

    // DI 주입!!!
    private final Repository userRepository;

    public RequestMapper(HttpRequest httpRequest, HttpResponse httpResponse, Repository userRepository) {
        this.httpRequest = httpRequest;
        this.httpResponse = httpResponse;
        this.controllers = new HashMap<>();
        this.userRepository = userRepository;

        // 컨트롤러 등록
        initializeControllers();
    }

    private void initializeControllers() {
        LoginController loginController = new LoginController();
        SignUpController signUpController = new SignUpController();

        // DI 완료!!!
        loginController.setUserRepository(userRepository);
        signUpController.setUserRepository(userRepository);

        // controllers 맵에 등록
        controllers.put("/user/signup", signUpController);
        controllers.put("/user/login", loginController);
        controllers.put("/user/userList", new ListController());
        controllers.put("/", new HomeController());
    }

    public void proceed() {
        Controller controller = controllers.get(httpRequest.getPath());
        Controller forwardController = new ForwardController();
        if (controller != null) {
            controller.execute(httpRequest, httpResponse);
        } else {
            //todo 404 Not Found 처리

            // GET 요청이고 루트패스가 아닌것으로 끝나는 경우 ForwardController 사용
            if (httpRequest.getMethod().equals("GET")) {
                forwardController.execute(httpRequest, httpResponse);
            }
        }
    }
}
