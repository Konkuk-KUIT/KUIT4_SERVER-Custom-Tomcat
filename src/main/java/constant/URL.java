package constant;

public enum URL {
    ROOT("./webapp"),
    INDEX("/index.html"),
    LOGIN_HTML("/user/login.html"),
    LOGIN("/user/login"),
    LIST("/user/list.html"),
    USER_LIST("/user/userList"),
    LOGIN_FAILED("/user/login_failed.html"),
    SIGNUP("/user/signup"),
    ;

    private final String url;

    URL(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
