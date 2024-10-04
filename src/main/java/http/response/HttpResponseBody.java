package http.response;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class HttpResponseBody {
    private final byte[] body;

    public HttpResponseBody(byte[] body) {
        this.body = body;
    }

    public static HttpResponseBody from(String filePath) throws IOException {
        byte[] body = Files.readAllBytes(Paths.get(filePath));
        return new HttpResponseBody(body);
    }

    public void writeBody(DataOutputStream dos) throws IOException {
        dos.write(body);
    }

    public int getLength() {
        return body.length;
    }
}
