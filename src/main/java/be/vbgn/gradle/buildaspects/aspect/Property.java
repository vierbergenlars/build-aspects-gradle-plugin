package be.vbgn.gradle.buildaspects.aspect;

import javax.annotation.Nullable;
import org.gradle.api.Named;

public interface Property<T> extends Named {

    @Override
    String getName();

    @Nullable
    T getValue();
}
