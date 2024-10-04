package webserver;

import db.MemoryUserRepository;
import model.User;

import java.io.IOException;
import java.nio.file.Files;

import static strings.FilePath.INDEX_HTML;
import static strings.FilePath.LOGIN_FAILED_HTML;

public class SignUpController extends Controller {
    HttpRequest httpRequest;
    HttpResponse httpResponse;

    private byte[] SignUp() throws IOException {
        byte[] body;
        String[] userInfo = null;

        if (httpRequest.getMethod().equals("GET")) {
            userInfo = httpRequest.getValueOfURLQuery(httpRequest.getUrl());
        }
        if (httpRequest.getMethod().equals("POST")) {
            userInfo = httpRequest.getValueOfQuery(httpRequest.getBody());
        }

        addNewUser(userInfo);
        httpResponse.response302Redirect("/index.html");

        body = Files.readAllBytes(INDEX_HTML.getPath());
        return body;
    }

    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        this.httpRequest = httpRequest;
        this.httpResponse = httpResponse;

        SignUp();
    }

    private void addNewUser(String[] userInfo) {
        User user = newUser(userInfo);
        MemoryUserRepository.getInstance().addUser(user);
    }

    private static User newUser(String[] userInfo) {
        return new User(userInfo[0], userInfo[1], userInfo[2], userInfo[3]);
    }
}
