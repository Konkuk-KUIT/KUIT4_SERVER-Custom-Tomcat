package webserver.controller;


import db.MemoryUserRepository;
import db.Repository;
import webserver.HttpRequest;
import webserver.HttpResponse;

public interface Controller {
    void execute(HttpRequest request, HttpResponse response);
}
