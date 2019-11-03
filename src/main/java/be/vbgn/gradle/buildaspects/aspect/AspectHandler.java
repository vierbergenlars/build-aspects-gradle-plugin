package be.vbgn.gradle.buildaspects.aspect;

import be.vbgn.gradle.buildaspects.internal.EventDispatcher;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.gradle.api.Action;

public class AspectHandler {

    private Set<String> aspectNames = new HashSet<>();
    private List<Aspect<?>> aspects = new ArrayList<>();

    private EventDispatcher<Aspect<?>> addAspectDispatcher = new EventDispatcher<>();

    public <T> Aspect<T> create(String name, Class<T> type, Action<WritableAspect<T>> configure) {
        if (!aspectNames.add(name)) {
            throw new IllegalArgumentException("Duplicate aspect with name " + name);
        }
        WritableAspect<T> aspect = new WritableAspectImpl<T>(name);
        configure.execute(aspect);
        aspects.add(aspect);
        addAspectDispatcher.fire(aspect);
        return aspect;
    }

    public Collection<Aspect<?>> getAspects() {
        return Collections.unmodifiableList(aspects);
    }

    public void aspectAdded(Action<Aspect<?>> listener) {
        addAspectDispatcher.addListener(listener);
    }
}
