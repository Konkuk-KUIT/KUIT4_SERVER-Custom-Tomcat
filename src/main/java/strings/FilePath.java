package strings;

import java.nio.file.Path;

public enum FilePath {
    INDEX_HTML(Path.of("webapp/index.html")),
    FORM_HTML(Path.of("webapp/user/form.html")),
    LOGIN_HTML(Path.of("webapp/user/login.html")),
    LOGIN_FAILED_HTML(Path.of("webapp/user/login_failed.html")),
    STYLES_CSS(Path.of("css/styles.css"));

    private final Path path;

    FilePath(Path path) {
        this.path = path;
    }

    public Path getPath() {
        return path;
    }
}
