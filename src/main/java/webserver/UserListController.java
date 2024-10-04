package webserver;

import java.io.IOException;

public class UserListController implements Controller{
    @Override
    public void execute(HttpResponse httpResponse, HttpRequest2 httpRequest) throws IOException {
        String cookieHeader = httpRequest.getHeader(HttpHeader.COOKIE.getHeader());

        if (cookieHeader != null && cookieHeader.contains("logined=true")) {
            httpResponse.forward("/user/list.html");
        } else {
            httpResponse.redirect("/user/login.html");
        }
    }
}
