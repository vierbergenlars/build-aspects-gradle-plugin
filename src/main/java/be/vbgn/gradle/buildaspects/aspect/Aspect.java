package be.vbgn.gradle.buildaspects.aspect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.gradle.api.Named;
import org.gradle.api.NonNullApi;

@NonNullApi
public class Aspect<T> implements Named {

    private final String name;

    private final List<T> options;

    Aspect(String name) {
        this.name = name;
        this.options = new ArrayList<>();
    }

    @Override
    public String getName() {
        return name;
    }

    public Collection<T> getOptions() {
        return Collections.unmodifiableCollection(options);
    }

    public Aspect<T> add(T option) {
        options.add(option);
        return this;
    }

    public Collection<Property<T>> getProperties() {
        return getOptions().stream()
                .map(o -> new Property<T>(name, o))
                .collect(Collectors.toUnmodifiableList());
    }

}
