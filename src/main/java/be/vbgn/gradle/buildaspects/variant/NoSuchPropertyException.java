package be.vbgn.gradle.buildaspects.variant;

import groovy.lang.MissingPropertyException;

public final class NoSuchPropertyException extends MissingPropertyException {

    private NoSuchPropertyException(String message, String name, Class<?> type) {
        super(message, name, type);
    }

    static NoSuchPropertyException forName(String name) {
        return new NoSuchPropertyException("A property with name '" + name + "' does not exist.", name, Variant.class);
    }
}
