package enums;

public enum UserQueryKey {
    USER_ID("userId"),
    PASSWORD("password"),
    NAME("name"),
    EMAIL("email");

    private final String key;

    UserQueryKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public static String getValue(UserQueryKey key) {
        return key.getKey();
    }
}