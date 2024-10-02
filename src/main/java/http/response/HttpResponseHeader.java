package http.response;

import constant.Format;

import java.io.DataOutputStream;
import java.io.IOException;

import static constant.Format.*;
import static constant.HttpHeader.*;

public class HttpResponseHeader {

    private final DataOutputStream dos;
    private final String COLON = ": ";
    //Carriage Return Line Feed
    private final String CRLF = " \r\n";

    public HttpResponseHeader(DataOutputStream dos) {
        this.dos = dos;
    }

    public void setLocation(String url) throws IOException {
        dos.writeBytes(LOCATION.getHeader()+COLON+url+CRLF);
    }

    public void setContentType(Format format) throws IOException {
        final String TYPE = "text/" + format.getFormat() + ";";
        final String CHARSET = "charset-utf-8";

        dos.writeBytes(CONTENT_TYPE.getHeader() + COLON+ TYPE + CHARSET + CRLF);
    }


    public void setContentLength(int length) throws IOException {
        dos.writeBytes(CONTENT_LENGTH.getHeader()+ COLON + length + CRLF);
    }

    public void setCookie(boolean isLogin) throws IOException {
        if(isLogin){
            final String LOGIN_TRUE = "logined=true";
            dos.writeBytes(SET_COOKIE.getHeader()+ COLON + LOGIN_TRUE + CRLF);
        }
    }
}
