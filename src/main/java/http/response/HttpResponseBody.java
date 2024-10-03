package http.response;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class HttpResponseBody {
    private final byte[] body;

    private HttpResponseBody(byte[] body) {
        this.body = body;
    }

    public static HttpResponseBody createHttpResponseBody(File file) throws IOException {
        byte[] body= Files.readAllBytes(file.toPath());
        return new HttpResponseBody(body);
    }

    public byte[] getBody(){
        return body;
    }

}
