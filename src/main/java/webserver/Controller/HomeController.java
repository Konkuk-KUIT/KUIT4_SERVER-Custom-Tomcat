package webserver.Controller;

import webserver.HttpRequest;
import webserver.HttpResponse;
import java.io.IOException;
import static webserver.enums.HttpUrl.HTTP_INDEX_HTML;

public class HomeController implements Controller {
    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        response.forward(HTTP_INDEX_HTML.getValue());
    }
}
