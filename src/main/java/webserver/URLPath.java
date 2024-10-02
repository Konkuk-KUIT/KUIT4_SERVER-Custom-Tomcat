package webserver;


public enum URLPath {
    INDEX("/index.html"),
    LOGIN("/user/login"),
    SIGNUP("/user/signup"),
    LIST("/user/list.html"),
    LOGINFAIL("/user/login_failed.html");


    private final String path;

    URLPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

}
