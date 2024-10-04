package http;

public enum Url {

    WEBAPP("./webapp"),
    ROOT("/"),
    INDEX("/index.html"),
    USER_FORM("/user/form.html"),
    USER_SIGNUP("/user/signup"),
    USER_LOGIN("/user/login"),
    USER_LOGIN_HTML("/user/login.html"),
    USER_LOGIN_FAILED("/user/login_failed.html"),
    USER_LIST("/user/userList"),
    USER_LIST_HTML("/user/list.html"),
    CSS_EXTENSION(".css");

    private String url;

    Url(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
