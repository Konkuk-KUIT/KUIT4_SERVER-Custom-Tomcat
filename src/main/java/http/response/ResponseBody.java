package http.response;

public class ResponseBody {

    private byte[] bodyContent;

    public ResponseBody(byte[] bodyContent) {
        this.bodyContent = bodyContent;
    }

    public int getBodyLength() {
        return bodyContent.length;
    }

    public byte[] getBodyContent() {
        return bodyContent;
    }
}
