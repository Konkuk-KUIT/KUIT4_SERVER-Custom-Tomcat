package Constant;

public enum HttpContentType {
    HTML("text/html"),
    CSS("text/css");

    String code;

    HttpContentType(String code){
        this.code = code;
    }

    public String getCode(){
        return code;
    }
}
