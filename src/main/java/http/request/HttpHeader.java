package http.request;

import http.constant.HttpHeaderType;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static http.constant.HttpHeaderType.*;

public class HttpHeader {
    private Map<HttpHeaderType,String> header;

    public HttpHeader(Map<HttpHeaderType, String> header) {
        this.header = header;
    }

    public static HttpHeader from(final BufferedReader br) throws IOException {
        final Map<HttpHeaderType,String> map = new LinkedHashMap<>();
        while(true) {
            final String line = br.readLine();
            if (line == null) break;
            if (line.isEmpty()) break;

            final String[] headerString = line.split(": ");
            validateHeader(headerString);
            final String key = headerString[0].trim();
            final String value = headerString[1].trim();
            HttpHeaderType headerType = getInstance(key);
            if (headerType != null) {
                map.put(headerType, value);
            }
        }
        return new HttpHeader(map);


    }


    public Map<HttpHeaderType, String> getHeader() {
        return header;
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
        header.put(headerName, value);
    }

    public Map<HttpHeaderType, String> getHeaderMap() {
        return this.header;
    }

    public void put(HttpHeaderType key, String value) {
        header.put(key,value);
    }
}
