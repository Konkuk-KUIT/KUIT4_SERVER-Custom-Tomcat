package webserver;

import db.MemoryUserRepository;

import java.io.IOException;

public class ListController extends Controller {
    HttpRequest httpRequest;
    HttpResponse httpResponse;

    private void printList() {
        if (httpRequest.cookieLoginTrue()) {
            System.out.println(MemoryUserRepository.getInstance().findAll());
        }
    }

    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        this.httpRequest = httpRequest;
        this.httpResponse = httpResponse;
    }
}
