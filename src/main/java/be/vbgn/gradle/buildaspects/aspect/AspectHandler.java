package be.vbgn.gradle.buildaspects.aspect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class AspectHandler {

    private Set<String> aspectNames = new HashSet<>();
    private List<Aspect<?>> aspects = new ArrayList<>();

    public <T> Aspect<T> create(String name, Class<T> type) {
        if (!aspectNames.add(name)) {
            throw new IllegalArgumentException("Duplicate aspect with name " + name);
        }
        Aspect<T> aspect = new Aspect<T>(name);
        aspects.add(aspect);
        return aspect;
    }

    public Collection<Aspect<?>> getAspects() {
        return Collections.unmodifiableList(aspects);
    }

    public Collection<Component> getComponents() {
        ComponentBuilder builder = new ComponentBuilder();
        for (Aspect<?> aspect : getAspects()) {
            builder = builder.addAspect(aspect);
        }

        return builder.build();
    }

}
