package http.response;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static http.HttpHeaders.*;

public class HttpResponseHeader {

    private final int contentLength;
    private final String contentType;
    private final String location;
    private final String setCookie;

    private HttpResponseHeader(int contentLength,
                              String contentType, String location, String setCookie) {
        this.contentLength = contentLength;
        this.contentType = contentType;
        this.location = location;
        this.setCookie = setCookie;
    }

    public static HttpResponseHeader createBasicHttpResponseHeader(File file) throws IOException {

        // 파일 내용을 읽어 바이트 배열로 변환
        byte[] body = Files.readAllBytes(file.toPath());
        int contentLength = body.length; // 파일 본문의 바이트 길이

        // MIME 타입 추출
        String contentType = Files.probeContentType(Paths.get(file.getPath()));

        String location = "";
        String setCookie = "";

        return new HttpResponseHeader(contentLength,contentType, location, setCookie);

    }

    public static HttpResponseHeader createRedirectHttpResponseHeader(String path){

        return new HttpResponseHeader(0,"", path, "");
    }

    public static HttpResponseHeader createCookieHttpResponseHeader(String path){

        return new HttpResponseHeader(0,"", path, LOGINED_TRUE.getHeader());
    }

    public String getHttpResponseHeader() {
        StringBuilder headerBuilder = new StringBuilder();

        if (!contentType.isEmpty()) {
            headerBuilder.append(CONTENT_TYPE.getHeader())
                    .append(": ")
                    .append(contentType)
                    .append("\r\n");
        }
        if (contentLength!=0) {
            headerBuilder.append(CONTENT_LENGTH.getHeader())
                    .append(": ")
                    .append(contentLength)
                    .append("\r\n");
        }
        if (!location.isEmpty()) {
            headerBuilder.append(LOCATION.getHeader())
                    .append(": ")
                    .append(location)
                    .append("\r\n");
        }
        if (!setCookie.isEmpty()) {
            headerBuilder.append(SET_COOKIE.getHeader())
                    .append(": ")
                    .append(setCookie)
                    .append("\r\n");
        }

        headerBuilder.append("\r\n");

        return headerBuilder.toString();
    }

}
