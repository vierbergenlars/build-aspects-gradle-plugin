package be.vbgn.gradle.buildaspects.aspect;

import java.util.Collection;
import org.gradle.api.Named;

public interface Aspect<T> extends Named {

    Collection<T> getOptions();

    Collection<Property<T>> getProperties();
}
