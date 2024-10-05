package enumPackage;

public enum URL {
    WEB_APP("./webapp"),

    INDEX_URL("/index.html"),
    LOGIN_URL("/user/login.html"),
    LIST_URL("/user/list.html"),
    USER_LIST_URL("/user/userList"),
    FORM_URL("/user/form.html"),
    LOGIN_FAILED_URL("/user/login_failed.html"),
    ;

    private final String url;

    URL(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
