package Enum;

public enum Url {
    ROOT("/"),
    HOME("/index.html"),
    HOME_HTML("webapp/index.html"),
    SIGNUP_FORM("/user/form.html"),
    SIGNUP_FORM_HTML("webapp/user/form.html"),
    SIGNUP("/user/signup"),
    LOGIN_FORM("/user/login.html"),
    LOGIN_FORM_HTML("webapp/user/login.html"),
    LOGIN("/user/login"),
    LOGIN_FAILED("/user/login_failed.html"),
    LOGIN_FAILED_HTML("webapp/user/login_failed.html"),
    USER_LIST("/user/userList"),
    LIST("/user/list.html"),
    LIST_HTML("webapp/user/list.html"),
    STYLES("/css/styles.css"),
    STYLES_CSS("webapp/css/styles.css"),

    ;

    private final String url;

    Url(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
