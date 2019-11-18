package be.vbgn.gradle.buildaspects.aspect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

class WritableAspectImpl<T> implements WritableAspect<T> {

    private final String name;
    private final Class<T> type;

    private final List<T> options;

    WritableAspectImpl(String name, Class<T> type) {
        this(name, type, new ArrayList<>());
    }

    private WritableAspectImpl(String name, Class<T> type, List<T> options) {
        this.name = name;
        this.type = type;
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
        if (!type.isInstance(option)) {
            throw new IllegalArgumentException("Options for aspect "+name+" must be of "+type.toGenericString());
        }
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
        return new WritableAspectImpl<>(name, type, Collections.unmodifiableList(options));
    }

}
