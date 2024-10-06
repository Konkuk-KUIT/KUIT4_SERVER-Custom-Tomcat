package http.request;

public enum Url {
    WEBAPP_PATH("./webapp"),
    INDEX_HTML("/index.html"),
    USER_SIGNUP("/user/signup"),
    USER_LOGIN("/user/login"),
    USER_LOGIN_HTML("/user/login.html"),
    USER_LIST("/user/userList"),
    USER_LOGIN_FAILED("/user/login_failed.html"),
    USER_LIST_HTML("/user/list.html"),
    ROOT("/");

    private final String path;

    Url(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
