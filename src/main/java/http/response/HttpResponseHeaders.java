package http.response;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpResponseHeaders {

    private final Map<String, String> headers = new HashMap<>();

    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    public String getHeader(String name) {
        return headers.get(name);
    }

    public void writeHeaders(DataOutputStream dos) throws IOException {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            dos.writeBytes(entry.getKey() + ": " + entry.getValue() + "\r\n");
        }
    }
}
