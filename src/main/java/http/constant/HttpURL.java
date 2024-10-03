package http.constant;

public enum HttpURL {
    ROOT("./webapp"),
    ROOT_USER("./webapp/user"),
    INDEX("/index.html"),
    LOGIN("/user/login"),
    LOGIN_HTML("/user/login.html"),
    LOGIN_FAILED("/user/login_failed.html"),
    SIGNUP("/user/signup"),
    USER_LIST("/user/userList"),
    USER_LIST_HTML("/user/list.html"),
    ;


    private String url;

    HttpURL(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
