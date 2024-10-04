package webserver;

import Controller.*;
import http.util.HttpRequest;
import http.util.HttpResponse;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import static enumClass.Url.*;
import static enumClass.HttpMethod.*;

public class RequestHandler implements Runnable {
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    private Controller controller = new ForwardController();

    public RequestHandler(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            DataOutputStream dos = new DataOutputStream(out);

            HttpRequest httpRequest = HttpRequest.from(br);
            HttpResponse httpResponse = new HttpResponse(dos);

            String path = httpRequest.getPath();

            // Decide the controller based on path
            if (httpRequest.isMethod(GET) && path.endsWith(".html")) {
                controller = new ForwardController();
            } else if (path.equals(ROOT.getValue())) {
                controller = new HomeController();
            } else if (path.equals(USER_SIGNUP.getValue())) {
                controller = new SignUpController();
            } else if (path.equals(USER_LOGIN.getValue())) {
                controller = new LoginController();
            } else if (path.equals(USER_USERLIST.getValue())) {
                controller = new ListController();
            }

            // Execute the controller
            controller.execute(httpRequest, httpResponse);

        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        }
    }
}
