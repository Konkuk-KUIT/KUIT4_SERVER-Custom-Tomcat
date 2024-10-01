package enums.route;

public enum StaticRoute {
    INDEX_HTML("/index.html"),
    LOGIN_HTML("/user/login.html"),
    LOGIN_FAILED_HTML("/user/login_failed.html"),
    USER_LIST_HTML("/user/list.html");

    private final String route;

    StaticRoute(String route) {
        this.route = route;
    }

    public String getRoute() {
        return route;
    }
}
