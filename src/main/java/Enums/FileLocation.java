package Enums;

public enum FileLocation {
    INDEX("/index.html"),USER_LIST("/user/list.html"),USER_LOGIN("user/login.html"), USER_LOGINFAILED("/user/login_failed.html");

    private String location;

    private FileLocation(String location) {
        this.location = location;
    }

    public String getLocation(){
        return location;
    }
}
