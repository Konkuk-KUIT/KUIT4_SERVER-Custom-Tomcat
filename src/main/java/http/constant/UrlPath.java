package http.constant;

public enum UrlPath {
    INDEX("/index.html"),
    SIGN_UP("/user/signup"),
    SIGN_UP_FORM("/user/form.html"),
    LOGIN("/user/login"),
    LOGIN_FORM("/user/login.html"),
    LOGIN_FAILED("/user/login_failed.html"),
    USER_LIST("/user/userList"),
    LIST("/user/list.html"),
    CSS(".css");

    private final String path;

    UrlPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
