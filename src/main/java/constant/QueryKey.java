package constant;

public enum QueryKey {

    USERID("userId"),
    PASSWORD("password"),
    NAME("name"),
    EMAIL("email");

    String value;

    private QueryKey(String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }
}