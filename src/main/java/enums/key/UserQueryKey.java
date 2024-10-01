package enums.key;

public enum UserQueryKey {
    USERID("userId"),
    PASSWORD("password"),
    NAME("name"),
    EMAIL("email");

    private final String value;

    UserQueryKey(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
