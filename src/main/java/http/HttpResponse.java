package http;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static http.util.HttpResponseUtils.getContentType;
import static http.util.HttpResponseUtils.getStatusLine;

public class HttpResponse {
    private final OutputStream out;
    private final Map<String, String> headers;

    public HttpResponse(OutputStream out) {
        this.out = out;
        this.headers = new HashMap<>();
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    // 클라이언트에게 파일을 전달(포워딩)하는 메서드.
    // 주어진 경로(path)의 파일을 읽어 응답 바디에 담아 200 OK 응답을 보낸다
    public void forward(String path) throws IOException {
        byte[] body = Files.readAllBytes(Paths.get("webapp" + path));
        String contentType = getContentType(path);
        sendResponse(200, body, contentType);
    }

    public void redirect(String path) throws IOException {
        addHeader("Location", path);
        sendResponse(302, new byte[0], null);
    }

    private void sendResponse(int statusCode, byte[] body, String contentType) throws IOException {
        DataOutputStream dos = new DataOutputStream(out);
        dos.writeBytes(getStatusLine(statusCode));

        if (contentType != null) {
            addHeader("Content-Type", contentType + ";charset=utf-8");
        }
        addHeader("Content-Length", String.valueOf(body.length));

        for (Map.Entry<String, String> header : headers.entrySet()) {
            dos.writeBytes(header.getKey() + ": " + header.getValue() + "\r\n");
        }

        dos.writeBytes("\r\n");
        if (body.length > 0) {
            dos.write(body, 0, body.length);
        }
        dos.flush();
    }
}