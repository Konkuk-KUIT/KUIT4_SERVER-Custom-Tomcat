package http.response;

import constant.URL;
import org.junit.jupiter.api.Test;
import webserver.RequestHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;

class HttpResponseTest {
    final String testDirectory = "./src/test/resources/";

    @Test
    public void forwardTest() throws Exception {
        //given
        String makeFilePath = testDirectory + "HttpForward.txt";
        HttpResponse httpResponse = new HttpResponse(outputStreamToFile(makeFilePath), Logger.getLogger(RequestHandler.class.getName()));

        //when
        httpResponse.forward(URL.INDEX.getUrl());

        //then
    }

    @Test
    public void setCssTest() throws Exception {
        //given
        String makeFilePath = testDirectory + "HttpCss.txt";
        HttpResponse httpResponse = new HttpResponse(outputStreamToFile(makeFilePath), Logger.getLogger(RequestHandler.class.getName()));

        //when
        httpResponse.setCss("/css/styles.css");

        //then
    }

    @Test
    public void redirectTest() throws Exception {
        //given
        String makeFilePath = testDirectory + "HttpRedirect.txt";
        HttpResponse httpResponse = new HttpResponse(outputStreamToFile(makeFilePath), Logger.getLogger(RequestHandler.class.getName()));

        //when
        httpResponse.redirect(URL.LOGIN.getUrl(), true);

        //then
    }

    private OutputStream outputStreamToFile(String path) throws IOException {
        return Files.newOutputStream(Paths.get(path));
    }
}