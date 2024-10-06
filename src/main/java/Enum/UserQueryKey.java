package Enum;

public enum UserQueryKey {

    USER_ID("userId"),
    PASSWORD("password"),
    NAME("name"),
    EMAIL("email"),
    ;

    private final String userQueryKey;

    UserQueryKey (String userQueryKey) {
        this.userQueryKey = userQueryKey;
    };

    public String getUserQueryKey() {
        return userQueryKey;
    }
}
