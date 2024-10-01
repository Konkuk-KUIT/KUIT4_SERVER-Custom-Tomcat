package http;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import static enums.route.StaticRoute.*;

class HttpResponseTest {

    HttpResponse httpResponse;

    @BeforeEach
    void setResponseFile() throws IOException {
        String testFilePath = "src/test/java/resource/outputCheckFile";
        httpResponse = new HttpResponse(new DataOutputStream(outputStreamToFile(testFilePath)));
    }

    private OutputStream outputStreamToFile(String path) throws IOException {
        return Files.newOutputStream(Paths.get(path));
    }

    @Test
    void forwardTest() throws IOException {
        httpResponse.forward(USER_LIST_HTML.getRoute());
    }

    @Test
    void redirectTest() {
        httpResponse.redirect(LOGIN_HTML.getRoute());
    }

    @Test
    void redirectSettingCookieTest() {
        httpResponse.redirectSettingCookie(INDEX_HTML.getRoute());
    }
}