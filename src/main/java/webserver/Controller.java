package webserver;

import java.io.IOException;

public class Controller {
    HttpRequest httpRequest;
    HttpResponse httpResponse;

    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        this.httpRequest = httpRequest;
        this.httpResponse = httpResponse;
    }
}
