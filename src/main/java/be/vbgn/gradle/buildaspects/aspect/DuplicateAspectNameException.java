package be.vbgn.gradle.buildaspects.aspect;

public final class DuplicateAspectNameException extends RuntimeException {

    private DuplicateAspectNameException(String message) {
        super(message);
    }

    static DuplicateAspectNameException forName(String name) {
        return new DuplicateAspectNameException("Duplicate aspect with name " + name);
    }
}
