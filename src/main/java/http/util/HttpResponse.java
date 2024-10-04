package http.util;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

    private HttpResponseStartLine httpResponseStartLine;
    private Map<String, String> httpHeader = new HashMap<>();
    private String body;

    private DataOutputStream dos;

    public HttpResponse(DataOutputStream dos) {
        this.dos = dos;
    }

    public void setStatusLine(String version, String statusCode, String statusMessage) {
        httpResponseStartLine = new HttpResponseStartLine(version, statusCode, statusMessage);
    }

    public void addHeader(String key, String value) {
        httpHeader.put(key, value);
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void sendResponse() throws IOException {
        // StartLine 전송
        dos.writeBytes(httpResponseStartLine.getVersion() + " " + httpResponseStartLine.getStatusCode() + " " + httpResponseStartLine.getMessage() + "\r\n");

        // Headers 전송
        for (Map.Entry<String, String> entry : httpHeader.entrySet()) {
            dos.writeBytes(entry.getKey() + ": " + entry.getValue() + "\r\n");
        }

        // 빈 줄을 추가하여 헤더와 바디 구분
        dos.writeBytes("\r\n");

        // Body 전송
        if (body != null && !body.isEmpty()) {
            dos.writeBytes(body);
        }

        dos.flush();
    }

    public void forward(String path) throws IOException {
        setStatusLine("HTTP/1.1", "200", "OK");
        addHeader("Content-Type", "text/html;charset=utf-8");

        // 파일 읽기 로직
        String fileContent = new String(Files.readAllBytes(Paths.get(path)));
        setBody(fileContent);

        sendResponse();
    }

    public void redirect(String path) throws IOException {
        setStatusLine("HTTP/1.1", "302", "Found");
        addHeader("Location", path);

        sendResponse();
    }
}
