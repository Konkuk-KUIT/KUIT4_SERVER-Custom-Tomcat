package webserver.enums;

public enum HttpUrl {
    HTTP_INDEX_HTML("/index.html"),
    HTTP_REGISTER_FORM_HTML("/user/form.html"),
    HTTP_LOGIN_HTML("/user/login.html"),
    HTTP_LIST_HTML("/user/list.html"),
    HTTP_LOGIN_FAILD_HTML("/user/login_failed.html"),
    HTTP_USER_LIST("/user/userList"),
    HTTP_USER_SIGNUP("/user/signup");

    private final String value;

    HttpUrl(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
