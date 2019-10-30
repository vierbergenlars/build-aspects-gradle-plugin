package be.vbgn.gradle.buildaspects.aspect;

import org.gradle.api.Named;

public class Property<T> implements Named {
    private final String name;

    private final T value;

    Property(String name, T value) {
        this.name = name;
        this.value = value;
    }


    @Override
    public String getName() {
        return name;
    }

    public T getValue() {
        return value;
    }
}
