package be.vbgn.gradle.buildaspects.aspect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

class WritableAspectImpl<T> implements WritableAspect<T> {

    private final String name;

    private final List<T> options;

    WritableAspectImpl(String name) {
        this(name, new ArrayList<>());
    }

    private WritableAspectImpl(String name, List<T> options) {
        this.name = name;
        this.options = options;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Collection<T> getOptions() {
        return Collections.unmodifiableCollection(options);
    }

    @Override
    public WritableAspect<T> add(T option) {
        options.add(option);
        return this;
    }

    @Override
    public Collection<Property<T>> getProperties() {
        return Collections.unmodifiableList(getOptions().stream()
                .map(o -> new Property<T>(name, o))
                .collect(Collectors.toList()));
    }

    Aspect<T> frozen() {
        return new WritableAspectImpl<T>(name, Collections.unmodifiableList(options));
    }

}
