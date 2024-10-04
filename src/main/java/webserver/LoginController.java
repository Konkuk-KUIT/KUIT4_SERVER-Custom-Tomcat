package webserver;

import db.MemoryUserRepository;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;

import static strings.FilePath.INDEX_HTML;
import static strings.FilePath.LOGIN_FAILED_HTML;

public class LoginController extends Controller {
    HttpRequest httpRequest;
    HttpResponse httpResponse;

    private byte[] Login() throws IOException {
        byte[] body;
        String[] userInfo = null;

        if (httpRequest.getMethod().equals("GET")) {
            userInfo = httpRequest.getValueOfURLQuery(httpRequest.getUrl());
        }
        if (httpRequest.getMethod().equals("POST")) {
            userInfo = httpRequest.getValueOfQuery(httpRequest.getBody());
        }

        body = Files.readAllBytes(INDEX_HTML.getPath());
        httpResponse.response302RedirectCookie("/index.html");

        if (MemoryUserRepository.getInstance().findUserById(userInfo[0]) == null) {
            body = Files.readAllBytes(LOGIN_FAILED_HTML.getPath());
        }
        return body;
    }

    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        this.httpRequest = httpRequest;
        this.httpResponse = httpResponse;

        Login();
    }


}
