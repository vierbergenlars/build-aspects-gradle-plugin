package be.vbgn.gradle.buildaspects.project.dsl;

import be.vbgn.gradle.buildaspects.variant.Variant;
import java.util.Objects;
import java.util.function.Predicate;
import org.gradle.api.Action;
import org.gradle.api.Project;

public interface BuildAspects {

    void when(Predicate<? super Variant> filter, Action<? super Project> configure);

    default void subprojects(Action<? super Project> configure) {
        when(c -> true, configure);
    }

    default void withVariant(String aspect, Object value, Action<? super Project> configure) {
        when(c -> Objects.equals(c.getProperty(aspect), value), configure);
    }

    default void withVariant(Predicate<? super Variant> filter, Action<? super Project> configure) {
        when(filter, configure);
    }
}
