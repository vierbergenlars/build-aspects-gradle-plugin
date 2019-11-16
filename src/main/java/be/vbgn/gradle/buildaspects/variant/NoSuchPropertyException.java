package be.vbgn.gradle.buildaspects.variant;

public class NoSuchPropertyException extends RuntimeException {

    public NoSuchPropertyException() {
    }

    public NoSuchPropertyException(String message) {
        super(message);
    }

    public NoSuchPropertyException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchPropertyException(Throwable cause) {
        super(cause);
    }

    static NoSuchPropertyException forName(String name) {
        return new NoSuchPropertyException("A property with name '" + name + "' does not exist.");
    }
}
