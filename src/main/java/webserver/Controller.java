package webserver;

import java.io.IOException;

public interface Controller {
    void execute(HttpResponse httpResponse, HttpRequest2 httpRequest) throws IOException;

}
