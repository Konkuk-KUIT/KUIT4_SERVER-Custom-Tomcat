package Controller;

import http.HttpRequest;
import http.HttpResponse;

import java.io.IOException;

import static Constant.HttpHeaderType.COOKIE;
import static Constant.Url.INDEX_HTML;
import static Constant.Url.USER_LIST_HTML;

public class ListController implements Controller {
    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        handleUserListRequest(httpRequest, httpResponse);
    }

    private void handleUserListRequest(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        if (httpRequest.getHeader(COOKIE.getValue()).equals("logined=true")) {                         // 쿠키 헤더 + 로그인 확인
            httpResponse.forward(USER_LIST_HTML.getMessage());
            return;
        }

        httpResponse.redirect(INDEX_HTML.getMessage());
    }
}

