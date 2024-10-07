package Constant;

public enum Url {

    WEBAPP("webapp"),
    INDEX_HTML("/index.html"),
    ROOT("/"),
    USER_FORM_HTML("/user/form.html"),
    USER_SIGNUP("/user/signup"),
    USER_LOGIN("/user/login"),
    USER_LOGIN_FAILED_HTML("/user/login_failed.html"),
    USER_USERLIST("/user/userList"),
    USER_LIST_HTML("/user/list.html"),
    URL_HTML(".html"),
    URL_CSS("CSS");


    final String message;

    Url(String message){
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
