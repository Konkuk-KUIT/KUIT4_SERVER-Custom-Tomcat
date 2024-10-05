package http;

import constants.HttpHeader;
import constants.StatusCode;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

    private final String version;
    private StatusCode statusCode;
    private final Map<HttpHeader, String> headers;
    private byte[] body;
    private final OutputStream outputStream;

    public HttpResponse(String version, OutputStream outputStream) {
        this.version = version;
        this.outputStream = outputStream;
        this.headers = new HashMap<>();
    }

    public void setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public void addHeader(HttpHeader header, String value) {
        headers.put(header, value);
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public void forward() throws IOException {
        DataOutputStream dos = new DataOutputStream(outputStream);

        // Start Line
        dos.writeBytes(version + " " + statusCode.getCode() + " " + statusCode.getMessage() + "\r\n");

        // Headers
        for (Map.Entry<HttpHeader, String> entry : headers.entrySet()) {
            dos.writeBytes(entry.getKey().getValue() + ": " + entry.getValue() + "\r\n");
        }
        dos.writeBytes("\r\n");

        // Body
        if (body != null) {
            dos.write(body);
        }
        dos.flush();
    }

    public void redirect(String location) throws IOException {
        setStatusCode(StatusCode.FOUND);
        addHeader(HttpHeader.LOCATION, location);
        forward();
    }
}