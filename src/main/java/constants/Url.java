package constants;

public enum Url {
    INDEX("/index.html"),
    USER_LIST("/user/list.html"),
    LOGIN("/user/login.html"),
    SIGNUP("/user/signup"),
    LOGIN_FAILED("/user/login_failed.html");

    private final String path;

    Url(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}