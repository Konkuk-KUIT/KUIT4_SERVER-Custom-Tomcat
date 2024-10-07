package Constant;

public enum UserQueryKey {

    USERID("userId"),
    PASSWORD("password"),
    NAME("name"),
    EMAIL("email");

    String value;

    private UserQueryKey(String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }
}
