package http.request;

import http.constant.HttpMethod;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

import static http.constant.HttpHeaderType.*;
import static http.util.IOUtils.*;

public class HttpRequest {
    private final HttpRequestStartLine startLine;
    private final HttpHeader header;
    private final String body;

    public HttpRequest(HttpRequestStartLine startLine, HttpHeader header, String body) {
        this.startLine = startLine;
        this.header = header;
        this.body = body;
    }

    public HttpRequestStartLine getStartLine() {
        return startLine;
    }

    public HttpHeader getHeader() {
        return header;
    }

    public String getBody() {
        return body;
    }

    public static HttpRequest from(BufferedReader br) throws IOException {
        final String startLine = br.readLine();
        if(startLine == null) {
            throw new IllegalArgumentException();
        }
        final HttpRequestStartLine httpRequestStartLine = HttpRequestStartLine.from(startLine);
        final HttpHeader httpRequestHeader = HttpHeader.from(br);
        final String HttpBody = readRequestBody(br,httpRequestHeader);

        return new HttpRequest(httpRequestStartLine,httpRequestHeader,HttpBody);
    }

    private static String readRequestBody(final BufferedReader br, final HttpHeader httpRequestHeader) throws IOException {
        if(!httpRequestHeader.header.containsKey(CONTENT_LENGTH)) return "";

        final int requestContentLength = Integer.parseInt(httpRequestHeader.getValue(CONTENT_LENGTH));
        return readData(br,requestContentLength);

    }

    public HttpMethod getHttpMethod(){
        return startLine.getMethod();
    }

   public Map<String, String> getQueryMap(){
        return this.getStartLine().queryString;
   }


}
