package webserver;



import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;


public class RequestHandler implements Runnable{
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    public RequestHandler(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()){
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            HttpResponse httpResponse = new HttpResponse(out);
            HttpRequest2 httpRequest = HttpRequest2.from(br);

            String filePath = httpRequest.getPath();

            /*// HTTP 메서드 확인하기
            if (httpRequest.getMethod() == HttpMethod.POST) {
                handlePostRequest(httpResponse, filePath, httpRequest);
            } else if (httpRequest.getMethod() == HttpMethod.GET) {
                handleGetRequest(httpResponse, filePath, httpRequest);
            }*/
            handleRequest(httpResponse, filePath, httpRequest);
        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

    private void handleRequest(HttpResponse httpResponse, String filePath, HttpRequest2 httpRequest)throws IOException{
        Controller controller = null;

        //로그인 처리
        if (filePath.startsWith(URL.USER_LOGIN.getPath())) {
            controller = new LoginController();
        }
        // 회원가입 처리 (POST 방식)
        else if (filePath.startsWith(URL.USER_SIGNUP.getPath())) {
            controller = new SignupController();
        }
        // 유저 리스트 요청 처리
        else if (filePath.startsWith(URL.USER_LIST.getPath())) {
            controller = new UserListController();
        }
        else{
            // CSS 파일 요청 처리
            if (filePath.endsWith(".css")) {
                httpResponse.forward("webapp" + filePath);
                return;
            }

            if ("/".equals(filePath)) {
                filePath = "/index.html";
            }
            // 파일 경로 설정
            String fullPath = "webapp" + filePath;
            File file = new File(fullPath);

            if (file.exists() && !file.isDirectory()) { // 파일이 존재하고 디렉토리가 아닌 경우
                httpResponse.forward(filePath);
                return;
            }
        }

        if (controller != null) {
            controller.execute(httpResponse, httpRequest);
        } else {
            httpResponse.notFound(); // 404 처리
        }
    }

}
