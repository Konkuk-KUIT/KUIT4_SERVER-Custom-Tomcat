package webserver;

public enum Path {
    ROOT_PATH("./webapp"),
    HOME_PATH("/index.html"),
    LOGIN_PATH("/user/login.html"),
    LIST_PATH("/user/list.html"),
    LOGIN_FAILED_PATH("/user/login_failed.html"),
    ;

    private final String path;

    Path(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
