package http.request;

import http.constant.HttpHeaderType;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static http.constant.HttpHeaderType.*;

public class HttpHeader {
    Map<HttpHeaderType,String> header;

    public HttpHeader(Map<HttpHeaderType, String> header) {
        this.header = header;
    }

    public static HttpHeader from(BufferedReader br) throws IOException {
        Map<HttpHeaderType,String> map = new HashMap<>();
        while(true){
            final String line = br.readLine();
            if(line == null) break;
            if(line.equals("")) break;

            String[] header = line.split(": ");
            validateHeader(header);
            String key = header[0].trim();
            String value = header[1].trim();
            HttpHeaderType headerType = getInstance(key);
            validateHeaderType(headerType, key);
            map.put(headerType, value);
        }
        return new HttpHeader(map);


    }

    private static void validateHeaderType(HttpHeaderType headerType, String key) {
        if (headerType == null) {
            throw new IllegalArgumentException("Unknown header type: " + key);
        }
    }

    private static void validateHeader(String[] header) {
        if(header.length < 2){
            throw new IllegalArgumentException("Invalid header format");
        }
    }

    public String getValue(HttpHeaderType headerName) {
        return this.header.get(headerName);
    }

    public void putHeader(HttpHeaderType headerName, String value) {
        this.header.put(headerName, value);
    }

}
