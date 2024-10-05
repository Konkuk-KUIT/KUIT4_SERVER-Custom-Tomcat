package controller;

import webserver.Response;
import webserver.Request;

import java.io.IOException;

public interface Controller {
    void execute(Request request, Response response) throws IOException;
}
