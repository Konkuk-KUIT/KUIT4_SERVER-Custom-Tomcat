package http.constant;

public enum HttpURL {
    ROOT("./webapp"),
    ROOT_USER("./webapp/user"),
    INDEX("/index.html"),
    LOGIN("/login.html"),
    LOGIN_FAILED("/login_failed.html"),
    USER_LIST("/list.html")
    ;


    private String url;

    HttpURL(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
