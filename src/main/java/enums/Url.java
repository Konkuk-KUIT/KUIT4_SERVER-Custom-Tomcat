package enums;

public enum Url {
    INDEX_HTML("/index.html"),
    SIGNUP("/user/signup"),
    LOGIN("/user/login"),
    USER_LIST("/user/userList"),
    LOGIN_FAILED_HTML("/user/login_failed.html"),
    USER_LIST_HTML("/user/list.html");

    private final String path;

    Url(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
