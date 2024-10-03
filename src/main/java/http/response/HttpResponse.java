package http.response;

import http.constant.HttpHeaderType;
import http.request.HttpHeader;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static http.constant.HttpHeaderType.*;
import static http.constant.HttpStatus.*;
import static http.constant.HttpURL.*;
//todo httpresponse header,body가 비어있음.
public class HttpResponse {
    private final OutputStream os;
    private byte[] body;
    private final HttpHeader header;
    private HttpResponseStartLine startLine = new HttpResponseStartLine();

    public HttpResponse(OutputStream outputStream) {
        this.os = new DataOutputStream(outputStream);
        this.body = new byte[0];
        header = new HttpHeader(new HashMap<>());
        header.putHeader(CONTENT_TYPE,"text/html;charset=utf-8");
    }


    public void writeResponseMessage() throws IOException{
        writeStartLineHeader();
        os.write(body);
        os.flush();
    }

    public void addBodyContents(String path) throws IOException {
        byte[] body = Files.readAllBytes(Paths.get(ROOT.getUrl() + path));
        put(CONTENT_LENGTH,String.valueOf(body.length));
        this.body = body;
    }

    public void forward(String targetPath) throws IOException {
        addBodyContents(targetPath);
        if(isHtml(targetPath)){
            writeResponseMessage();
            return;
        }
        put(CONTENT_TYPE,"text/css");
        writeResponseMessage();
    }

    private boolean isHtml(String targetPath) {
        String[] paths = targetPath.split("\\.");
        return paths[paths.length-1].equals("html");
    }

    public HttpHeader getHeader() {
        return header;
    }

    public HttpResponseStartLine getStartLine() {
        return startLine;
    }

    public void response302(String path) throws IOException {
        startLine.setHttpStatus(REDIRECT);
        startLine.setStatusCode("302");
        put(LOCATION,path);
        writeStartLineHeader();


    }

    private void writeStartLineHeader() throws IOException {
        os.write((startLine.getVersion() + " " + startLine.getStatusCode() + " " + startLine.getHttpStatus() + "\r\n").getBytes());
        Map<HttpHeaderType, String> headerMap = header.getHeaderMap();
        for (Map.Entry<HttpHeaderType, String> headerEntry : headerMap.entrySet()) {
            os.write((headerEntry.getKey() + ": " + headerEntry.getValue() + "\r\n").getBytes());
        }
        os.write("\r\n".getBytes());
    }

    public void put(HttpHeaderType key, String value) {
        header.put(key, value);
    }






}
