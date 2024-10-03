package http.util;

public class HttpResponseUtils {

    public static String getContentType(String path) {
        if (path.endsWith(".css")) {
            return "text/css";
        }
        return "text/html";
    }

    public static String getStatusLine(int statusCode) {
        switch (statusCode) {
            case 200:
                return "HTTP/1.1 200 OK \r\n";
            case 302:
                return "HTTP/1.1 302 Found \r\n";
            case 404:
                return "HTTP/1.1 404 Not Found \r\n";
            default:
                return "HTTP/1.1 500 Internal Server Error \r\n";
        }
    }


}
