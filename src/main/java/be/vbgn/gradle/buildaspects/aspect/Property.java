package be.vbgn.gradle.buildaspects.aspect;

import org.gradle.api.Named;

public interface Property<T> extends Named {

    T getValue();
}
