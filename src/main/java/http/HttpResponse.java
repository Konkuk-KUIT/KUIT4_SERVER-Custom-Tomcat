package http;

import enums.http.HttpStatus;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import static enums.extension.FileExtension.CSS;
import static enums.extension.FileExtension.HTML;
import static enums.http.header.EntityHeader.CONTENTLENGTH;
import static enums.http.header.EntityHeader.CONTENTTYPE;
import static enums.http.header.ResponseHeader.LOCATION;
import static enums.http.header.ResponseHeader.SETCOOKIE;

public class HttpResponse {
    private static final Logger log = Logger.getLogger(HttpResponse.class.getName());
    private DataOutputStream dos;

    public HttpResponse(DataOutputStream dos) {
        this.dos = dos;
    }

    public void forward(String path) throws IOException {
        String relativePath = "webapp"+path;
        byte[] body = Files.readAllBytes(Paths.get(relativePath));

        if(path.endsWith(HTML.addFrontPoint())){
            sendHeader(dos, HTML.getValue(), body.length);
        }
        if(path.endsWith(CSS.addFrontPoint())){
            sendHeader(dos, CSS.getValue(), body.length);
        }
        sendBody(dos, body);
    }

    public void redirect(String location){
        try {
            dos.writeBytes(HttpStatus.REDIRECT.getValue());
            dos.writeBytes(LOCATION.toResponseString() + location + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    public void redirectSettingCookie(String location){
        try {
            dos.writeBytes(HttpStatus.REDIRECT.getValue());
            dos.writeBytes(SETCOOKIE.toResponseString()+ "logined=true" + "\r\n");
            dos.writeBytes(LOCATION.toResponseString() + location + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void sendHeader(DataOutputStream dos, String extension, int lengthOfBodyContent) {
        try {
            dos.writeBytes(HttpStatus.OK.getValue());
            dos.writeBytes(CONTENTTYPE.toResponseString() + "text/" + extension + ";charset=utf-8\r\n");
            dos.writeBytes(CONTENTLENGTH.toResponseString() + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void sendBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }
}
