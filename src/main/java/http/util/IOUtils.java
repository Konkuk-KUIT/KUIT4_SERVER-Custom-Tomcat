package http.util;

import java.io.BufferedReader;
import java.io.IOException;

//HTTP 요청의 데이터를 읽어오는 유틸리티 클래스
public class IOUtils {
    /**
     *
     * @param br
     * socket으로부터 가져온 InputStream
     *
     * @param contentLength
     * 헤더의 Content-Length의 값이 들어와야한다.
     *
     */
    public static String readData(BufferedReader br, int contentLength) throws IOException {    //주어진 버퍼에서 특정 길이만큼 데이터를 읽어, 문자열로 변환
        char[] body = new char[contentLength];
        br.read(body, 0, contentLength);
        return String.copyValueOf(body);
    }
}
