package webserver;

public enum ContentType {
    HTML("text/html;charset=utf-8"),
    CSS("text/css;charset=utf-8"),
    JS("application/javascript;charset=utf-8"),
    PNG("image/png"),
    JPEG("image/jpeg"),
    OCTET_STREAM("application/octet-stream");

    private final String type;

    ContentType(String type){
        this.type = type;
    }

    public String getType(){
        return type;
    }

}
