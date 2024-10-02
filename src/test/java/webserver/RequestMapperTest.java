package webserver;

import http.HttpRequest;
import http.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class RequestMapperTest {
    HttpRequest httpRequest;
    HttpResponse httpResponse;

    @BeforeEach
    void setResponseFile() throws IOException {
        String testFilePath = "src/test/java/resource/outputCheckFile";
        httpResponse = new HttpResponse(new DataOutputStream(outputStreamToFile(testFilePath)));
    }

    private OutputStream outputStreamToFile(String path) throws IOException {
        return Files.newOutputStream(Paths.get(path));
    }

    private RequestMapper setRequestMapperFromFilePath(String filePath) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filePath));

        httpRequest = HttpRequest.from(br);
        RequestMapper requestMapper = RequestMapper.from(httpRequest, httpResponse);
        return requestMapper;
    }

    @Test
    @DisplayName("ForwardController Case - HTML")
    void proceedForwardHtml() throws IOException {
        String requestFilePath = "src/test/java/resource/webserver/httpRequestForwardHtml";

        RequestMapper requestMapper = setRequestMapperFromFilePath(requestFilePath);
        requestMapper.proceed();
    }

    @Test
    @DisplayName("ForwardController Case - CSS")
    void proceedForwardCss() throws IOException {
        String requestFilePath = "src/test/java/resource/webserver/httpRequestForwardCss";

        RequestMapper requestMapper = setRequestMapperFromFilePath(requestFilePath);
        requestMapper.proceed();
    }

    @Test
    @DisplayName("HomeController Case")
    void proceedHome() throws IOException {
        String requestFilePath = "src/test/java/resource/webserver/httpRequestHome";

        RequestMapper requestMapper = setRequestMapperFromFilePath(requestFilePath);
        requestMapper.proceed();
    }

    @Test
    @DisplayName("ListController Case - With Cookie")
    void proceedListCookie() throws IOException {
        String requestFilePath = "src/test/java/resource/webserver/httpRequestListWithCookie";

        RequestMapper requestMapper = setRequestMapperFromFilePath(requestFilePath);
        requestMapper.proceed();
    }

    @Test
    @DisplayName("ListController Case - No Cookie")
    void proceedListFail() throws IOException {
        String requestFilePath = "src/test/java/resource/webserver/httpRequestListNoCookie";

        RequestMapper requestMapper = setRequestMapperFromFilePath(requestFilePath);
        requestMapper.proceed();
    }

    @Test
    @DisplayName("LoginController Case")
    void proceedLogin() throws IOException {
        String requestFilePath = "src/test/java/resource/webserver/httpRequestLogin";

        RequestMapper requestMapper = setRequestMapperFromFilePath(requestFilePath);
        requestMapper.proceed();
    }

    @Test
    @DisplayName("SignUpController Case")
    void proceedSignUp() throws IOException {
        String requestFilePath = "src/test/java/resource/webserver/httpRequestSignUp";

        RequestMapper requestMapper = setRequestMapperFromFilePath(requestFilePath);
        requestMapper.proceed();
    }
}