package model;

public enum UserQueryKey {
    ID("userId"),
    PWD("password"),
    NAME("name"),
    EMAIL("email")
    ;


    private String key;

    UserQueryKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
