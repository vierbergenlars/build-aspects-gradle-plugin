package be.vbgn.gradle.buildaspects.variant;

import groovy.lang.MissingPropertyException;

public final class NoSuchPropertyException extends MissingPropertyException {

    private NoSuchPropertyException(String message, String name, Class<?> type) {
        super(message, name, type);
    }

    static NoSuchPropertyException forName(String name) {
        return new NoSuchPropertyException("A property with name '" + name + "' does not exist.", name, Variant.class);
    }

    static NoSuchPropertyException forClassTypeMismatch(String name, Class<?> actualClass, Class<?> expectedClass) {
        return new NoSuchPropertyException("A property with name '" + name + "' and class '" + expectedClass.getName()
                + "' does not exist. Property '" + name + "' has a value of type '" + actualClass.getName() + "'.",
                name, Variant.class);
    }
}
