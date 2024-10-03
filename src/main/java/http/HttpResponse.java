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
import static enums.http.HttpStatus.*;
import static enums.http.header.EntityHeader.CONTENTLENGTH;
import static enums.http.header.EntityHeader.CONTENTTYPE;
import static enums.http.header.ResponseHeader.LOCATION;
import static enums.http.header.ResponseHeader.SETCOOKIE;

public class HttpResponse {
    private static final Logger log = Logger.getLogger(HttpResponse.class.getName());
    private DataOutputStream dos;
    private StringBuilder responseHeaderSB;

    public HttpResponse(DataOutputStream dos) {
        this.dos = dos;
        this.responseHeaderSB = new StringBuilder();
    }

    public void forward(String path) throws IOException {
        String relativePath = "webapp"+path;
        byte[] body = Files.readAllBytes(Paths.get(relativePath));

        setStatusLine(HttpStatus.OK);

        if(path.endsWith(HTML.addFrontPoint())) addContentInfo(HTML.getValue(), body.length);
        if(path.endsWith(CSS.addFrontPoint())) addContentInfo(CSS.getValue(), body.length);

        sendHeader();
        sendBody(body);
    }

    public void redirect(String location){
        setStatusLine(REDIRECT);
        addLocation(location);
        sendHeader();
    }

    public void redirectSettingCookie(String location){
        setStatusLine(REDIRECT);
        addSetCookie();
        addLocation(location);
        sendHeader();
    }

    private void setStatusLine(HttpStatus httpStatus){
        responseHeaderSB.append(httpStatus.getValue());
    }

    private void addContentInfo(String extension, int lengthOfBodyContent){
        responseHeaderSB.append(CONTENTTYPE.toResponseString() + "text/" + extension + ";charset=utf-8\r\n");
        responseHeaderSB.append(CONTENTLENGTH.toResponseString() + lengthOfBodyContent + "\r\n");
    }

    private void addLocation(String location){
        responseHeaderSB.append(LOCATION.toResponseString() + location + "\r\n");
    }

    private void addSetCookie(){
        responseHeaderSB.append(SETCOOKIE.toResponseString()+ "logined=true" + "\r\n");
    }

    private void sendHeader() {
        responseHeaderSB.append("\r\n");
        try{
            dos.writeBytes(responseHeaderSB.toString());
            responseHeaderSB.setLength(0);
            dos.flush();
        }
        catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void sendBody(byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }
}
