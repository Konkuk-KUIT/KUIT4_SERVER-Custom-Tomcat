package webserver.enums;

public enum HttpUrl {
    HTTP_INDEX_HTML("/index.html"),
    HTTP_LOGIN_HTML("/user/login.html"),
    HTTP_LOGIN("/user/login"),
    HTTP_LIST_HTML("/user/list.html"),
    HTTP_LOGIN_FAILD_HTML("/user/login_failed.html"),
    HTTP_USER_LIST("/user/userList"),
    HTTP_USER_SIGNUP("/user/signup"),
    HTTP_ROOT("/"),
    HTTP_END_HTML(".html"),
    HTTP_END_CSS(".css");

    private final String value;

    HttpUrl(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
