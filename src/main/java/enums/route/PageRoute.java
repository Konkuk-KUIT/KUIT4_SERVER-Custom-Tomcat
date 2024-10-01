package enums.route;

public enum PageRoute {
    HOME("/"),
    SIGNUP("/user/signup"),
    LOGIN("/user/login"),
    USER_LIST("/user/userList");

    private final String route;

    PageRoute(String route) {
        this.route = route;
    }

    public String getRoute() {
        return route;
    }
}
