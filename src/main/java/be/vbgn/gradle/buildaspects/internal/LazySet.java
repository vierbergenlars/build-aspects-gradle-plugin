package be.vbgn.gradle.buildaspects.internal;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

public class LazySet<T> extends AbstractSet<T> {
    private Set<? extends Set<T>> sets;

    public LazySet(Set<? extends Set<T>> sets) {
        this.sets = sets;
    }

    @Override
    public Iterator<T> iterator() {
        return sets.stream()
                .flatMap(Collection::stream)
                .iterator();
    }

    @Override
    public int size() {
        return sets.stream()
                .mapToInt(Collection::size)
                .sum();
    }
}
