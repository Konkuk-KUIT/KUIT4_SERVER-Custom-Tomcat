package constant;

public enum Url {

    WEBAPP("webapp"),
    ROOT("/"),
    INDEX_HTML("/index.html"),
    USER_FORM_HTML("/user/form.html"),
    USER_SIGNUP("/user/signup"),
    USER_LOGIN("/user/login"),
    USER_LOGIN_HTML("/user/login.html"),
    USER_LOGIN_FAILED_HTML("/user/login_failed.html"),
    USER_USERLIST("/user/userList"),
    USER_LIST_HTML("/user/list.html"),
    CSS_EXTENSION(".css"),
    HTML_EXTENSION(".html");

    final String url;

    private Url(String url){
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}