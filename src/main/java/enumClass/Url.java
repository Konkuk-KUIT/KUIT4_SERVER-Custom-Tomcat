package enumClass;

public enum Url {

    WEBAPP("webapp"),
    ROOT("/"),
    INDEX_PAGE("/index.html"),
    USER_FORM_PAGE("/user/form.html"),
    USER_SIGNUP("/user/signup"),
    USER_LOGIN("/user/login"),
    USER_LOGIN_PAGE("/user/login.html"),
    USER_LOGIN_FAILED_PAGE("/user/login_failed.html"),
    USER_USERLIST("/user/userList"),
    USER_LIST_PAGE("/user/list.html"),
    STYLE_CSS(".css");

    final String value;

    private Url(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
