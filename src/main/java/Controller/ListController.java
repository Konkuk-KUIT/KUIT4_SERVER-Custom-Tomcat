package Controller;

import http.util.HttpRequest;
import http.util.HttpResponse;

public class ListController implements Controller {
    @Override
    public void execute(HttpRequest request, HttpResponse response) throws Exception {
        if ("logined=true".equals(request.getHeaders().get("Cookie"))) {
            response.forward("/user/list.html");
        } else {
            response.redirect("/user/login.html");
        }
    }
}
