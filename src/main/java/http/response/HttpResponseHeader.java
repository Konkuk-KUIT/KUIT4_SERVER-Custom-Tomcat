package http.response;

import constant.Format;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static constant.Format.*;
import static constant.HttpHeader.*;

public class HttpResponseHeader {

    private final OutputStream dos;
    private final String COLON = ": ";
    //Carriage Return Line Feed
    private final String CRLF = " \r\n";

    public HttpResponseHeader(OutputStream dos) {
        this.dos = dos;
    }

    public void setLocation(String url) throws IOException {
        dos.write((LOCATION.getHeader()+COLON+url+CRLF).getBytes());
    }

    public void setContentType(Format format) throws IOException {
        final String TYPE = "text/" + format.getFormat() + ";";
        final String CHARSET = "charset-utf-8";

        dos.write((CONTENT_TYPE.getHeader() + COLON+ TYPE + CHARSET + CRLF).getBytes());
    }


    public void setContentLength(int length) throws IOException {
        dos.write((CONTENT_LENGTH.getHeader()+ COLON + length + CRLF).getBytes());
    }

    public void setCookie(boolean isLogin) throws IOException {
        if(isLogin){
            final String LOGIN_TRUE = "logined=true";
            dos.write((SET_COOKIE.getHeader()+ COLON + LOGIN_TRUE + CRLF).getBytes());
        }
    }
}
