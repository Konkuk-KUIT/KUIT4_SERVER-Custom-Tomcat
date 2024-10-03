package http.response;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

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

        // 실제 파일 내용을 읽어 바이트 배열로 변환
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

        return new HttpResponseHeader(0,"", path, "logined=true");
    }

    public String getHttpResponseHeader() {
        StringBuilder headerBuilder = new StringBuilder();

        // Content-Type
        if (!contentType.isEmpty()) {
            headerBuilder.append("Content-Type: ")
                    .append(contentType)
                    .append("\r\n");
        }

        // Content-Length
        if (contentLength!=0) {
            headerBuilder.append("Content-Length: ")
                    .append(contentLength)
                    .append("\r\n");
        }

        // Location (리다이렉션이 필요한 경우)
        if (!location.isEmpty()) {
            headerBuilder.append("Location: ")
                    .append(location)
                    .append("\r\n");
        }

        // Set-Cookie (필요 시)
        if (!setCookie.isEmpty()) {
            headerBuilder.append("Set-Cookie: ")
                    .append(setCookie)
                    .append("\r\n");
        }

        // 헤더 끝에 빈 줄 추가
        headerBuilder.append("\r\n");

        return headerBuilder.toString();
    }


}
