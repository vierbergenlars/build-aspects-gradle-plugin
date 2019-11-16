package be.vbgn.gradle.buildaspects.variant;

public final class NoSuchPropertyException extends RuntimeException {

    private NoSuchPropertyException(String message) {
        super(message);
    }

    static NoSuchPropertyException forName(String name) {
        return new NoSuchPropertyException("A property with name '" + name + "' does not exist.");
    }
}
