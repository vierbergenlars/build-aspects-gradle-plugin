package be.vbgn.gradle.buildaspects.aspect;

public class DuplicateAspectNameException extends RuntimeException {

    public DuplicateAspectNameException() {
    }

    public DuplicateAspectNameException(String message) {
        super(message);
    }

    public DuplicateAspectNameException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateAspectNameException(Throwable cause) {
        super(cause);
    }

    static DuplicateAspectNameException forName(String name) {
        return new DuplicateAspectNameException("Duplicate aspect with name " + name);
    }
}
