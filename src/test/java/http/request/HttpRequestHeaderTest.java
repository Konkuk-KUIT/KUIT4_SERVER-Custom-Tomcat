package http.request;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import static http.constant.HttpHeaderType.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpRequestHeaderTest {
    private final String Directory = "./src/test/resource/";
    private final String Path = "RequestHeaderTest.txt";
    private final String InvalidPath = "InvalidHeaderTest.txt";
    @Test
    public void header_정상_동작() throws IOException {
        HttpHeader header = HttpHeader.from(bufferedReaderFromFile(Directory + Path));
        assertThat(header.getValue(CONNECTION)).isEqualTo("keep-alive");
        assertThat(header.getValue(CONTENT_LENGTH)).isEqualTo("40");
        assertThat(header.getValue(HOST)).isEqualTo("localhost:8080");
    }

    @Test
    public void header_예외() throws IOException {
        assertThatThrownBy(()->HttpHeader.from(bufferedReaderFromFile(Directory + InvalidPath)))
                .isInstanceOf(IllegalArgumentException.class);

    }


    private BufferedReader bufferedReaderFromFile(String path) throws IOException {
        return new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(path))));
    }
}
