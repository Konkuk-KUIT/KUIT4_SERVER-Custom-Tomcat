package webserver.Controller;

import webserver.HttpRequest;
import webserver.HttpResponse;
import java.io.IOException;
import static webserver.enums.HttpUrl.HTTP_LIST_HTML;
import static webserver.enums.HttpUrl.HTTP_LOGIN_HTML;

public class ListController implements Controller {
    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        //로그인 되어있으면 리스트 HTML 전송
        if (isLoggedIn(request)) {
            response.forward(HTTP_LIST_HTML.getValue());
        } else {
            response.redirect(HTTP_LOGIN_HTML.getValue(), false);
        }
    }

    private boolean isLoggedIn(HttpRequest request) {
        String cookieHeader = request.getHeader("Cookie");
        boolean isLoggedIn = cookieHeader != null && cookieHeader.contains("logined=true");
        return isLoggedIn;
    }
}
