package http.util;

import org.junit.jupiter.api.Test;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class HttpResponseTest {

    private OutputStream outputStreamToFile(String path) throws IOException {
        return Files.newOutputStream(Paths.get(path));
    }


    @Test
    void 쓰기_확인() throws IOException {
//        OutputStream outputStream = outputStreamToFile("src/test/resource/message.txt");
//        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
//
//        HttpResponse httpResponse = new HttpResponse(dataOutputStream);
//
//        httpResponse.forward("/index.html");
    }

}