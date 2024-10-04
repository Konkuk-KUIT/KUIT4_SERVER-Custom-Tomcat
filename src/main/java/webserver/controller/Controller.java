package webserver.controller;


import db.MemoryUserRepository;
import db.Repository;
import webserver.HttpRequest;
import webserver.HttpResponse;

import java.io.IOException;

public interface Controller {
    void execute(HttpRequest request, HttpResponse response) throws IOException;
}
