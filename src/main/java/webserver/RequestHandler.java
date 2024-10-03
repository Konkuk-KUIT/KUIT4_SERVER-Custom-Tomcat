package webserver;

import controller.*;
import db.MemoryUserRepository;
import db.Repository;
import http.constant.HttpURL;
import http.request.HttpRequest;
import http.request.HttpRequestStartLine;
import http.request.RequestMapper;
import http.response.HttpResponse;
import model.User;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static http.constant.HttpHeaderType.*;
import static http.constant.HttpMethod.*;
import static http.constant.HttpStatus.*;
import static http.constant.HttpURL.*;
import static http.util.HttpRequestUtils.parseQueryParameter;
import static http.util.IOUtils.readData;
import static model.UserQueryKey.*;

public class RequestHandler implements Runnable{
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());
    private final Repository repository;
    private Controller controller = new ForwardController();
    public RequestHandler(Socket connection) {
        this.connection = connection;
        this.repository = MemoryUserRepository.getInstance();
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()){
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            HttpRequest httpRequest = HttpRequest.from(br);
            HttpResponse httpResponse = new HttpResponse(dos);

            RequestMapper requestMapper = new RequestMapper(httpResponse,httpRequest);
            requestMapper.proceed();


        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }



}