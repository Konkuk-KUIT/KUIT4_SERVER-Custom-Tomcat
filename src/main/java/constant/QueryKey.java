package constant;

public enum QueryKey {

    USERID("userId"),
    PASSWORD("password"),
    NAME("name"),
    EMAIL("email");

    String key;

    private QueryKey(String key){
        this.key = key;
    }

    public String getKey(){
        return key;
    }
}