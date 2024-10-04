package webserver;

import controller.*;
//import webserver.controller.*;
import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestHandler implements Runnable{
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());
    private final Map<String, Controller> controllerMap;

    public RequestHandler(Socket connection) {
        this.connection = connection;
        this.controllerMap = new HashMap<>();

        controllerMap.put(PathName.SIGNUP.getPathName(), new SignUpController());
        controllerMap.put(PathName.LOGIN.getPathName(), new LoginController());
        controllerMap.put(PathName.USER_LIST.getPathName(), new UserListController());
        controllerMap.put(PathName.HOME.getPathName(), new HomeController());
    }



    // GET, POST 요청인지 확인해서 각각의 핸들 메서드 호출
    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()){
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            Request request = Request.from(br);
            Response response = new Response(dos);

            String path = request.getPath();

            Controller controller = controllerMap.get(path);
            if(controller != null){
                controller.execute(request, response);
            }else{
                response.forward(path);
            }

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

}
