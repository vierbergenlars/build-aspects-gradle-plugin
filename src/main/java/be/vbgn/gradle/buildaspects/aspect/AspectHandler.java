package be.vbgn.gradle.buildaspects.aspect;

import be.vbgn.gradle.buildaspects.internal.EventDispatcher;
import be.vbgn.gradle.buildaspects.variant.Variant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import org.gradle.api.Action;
import org.gradle.api.plugins.ExtensionAware;

public abstract class AspectHandler implements ExtensionAware {

    private final Set<String> aspectNames = new HashSet<>();
    private final List<Aspect<?>> aspects = new ArrayList<>();

    private final EventDispatcher<Aspect<?>> addAspectDispatcher = new EventDispatcher<>();
    private final EventDispatcher<CalculatedPropertyBuilder<?>> calculatedPropertyBuilderEventDispatcher = new EventDispatcher<>();

    @SafeVarargs
    public final <T> Aspect<T> create(String name, T item0, T... items) {
        List<T> itemsList = new ArrayList<>(items.length + 1);
        itemsList.add(item0);
        itemsList.addAll(Arrays.asList(items));
        return create(name, (Class<T>) item0.getClass(), itemsList);
    }

    public <T> Aspect<T> create(String name, Class<T> type, Iterable<? extends T> items) {
        return create(name, type, a -> items.forEach(a::add));
    }

    public <T> Aspect<T> create(String name, Class<T> type, Action<? super WritableAspect<T>> configure) {
        if (!aspectNames.add(name)) {
            throw DuplicateAspectNameException.forName(name);
        }
        WritableAspectImpl<T> aspect = new WritableAspectImpl<>(name, type);
        configure.execute(aspect);
        Aspect<T> frozenAspect = aspect.frozen();
        aspects.add(frozenAspect);
        addAspectDispatcher.fire(frozenAspect);
        return frozenAspect;
    }

    public <T> void calculated(String name, Function<? super Variant, ? extends T> calculator) {
        if (!aspectNames.add(name)) {
            throw DuplicateAspectNameException.forName(name);
        }
        calculatedPropertyBuilderEventDispatcher.fire(new CalculatedPropertyBuilder<>(name, calculator));
    }

    public Collection<Aspect<?>> getAspects() {
        return Collections.unmodifiableList(aspects);
    }

    public void aspectAdded(Action<Aspect<?>> listener) {
        addAspectDispatcher.addListener(listener);
    }

    public void calculatedPropertyAdded(Action<CalculatedPropertyBuilder<?>> listener) {
        calculatedPropertyBuilderEventDispatcher.addListener(listener);
    }
}
