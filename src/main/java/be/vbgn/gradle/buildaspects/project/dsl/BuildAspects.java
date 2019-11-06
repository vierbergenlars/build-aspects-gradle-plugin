package be.vbgn.gradle.buildaspects.project.dsl;

import be.vbgn.gradle.buildaspects.component.Component;
import java.util.Objects;
import java.util.function.Predicate;
import org.gradle.api.Action;
import org.gradle.api.Project;

public interface BuildAspects {

    void when(Predicate<? super Component> filter, Action<? super Project> configure);

    default void withAspect(String aspect, Object value, Action<? super Project> configure) {
        when(c -> Objects.equals(c.getProperty(aspect), value), configure);
    }
}
