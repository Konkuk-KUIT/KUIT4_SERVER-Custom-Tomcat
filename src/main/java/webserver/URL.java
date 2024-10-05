package webserver;

public enum URL {
    USER_LOGIN("/user/login"),
    USER_SIGNUP("/user/signup"),
    USER_LIST("/user/userList"),
    INDEX("/");

    private final String path;

    URL(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
