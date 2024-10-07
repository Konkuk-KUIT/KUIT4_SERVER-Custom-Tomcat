package http;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class HttpResBody {
    private byte[] body;

    public void loadBodyFromFile(String path) throws IOException {              // 파일에서 본문을 읽어들임
        this.body = Files.readAllBytes(Paths.get("webapp" + path));
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public byte[] getBody() {
        return body;
    }

    public int getBodyLength() {
        return body.length;
    }
}