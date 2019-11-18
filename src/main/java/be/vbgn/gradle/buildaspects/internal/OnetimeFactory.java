package be.vbgn.gradle.buildaspects.internal;

import java.util.Objects;
import java.util.function.Function;
import javax.annotation.Nullable;

public class OnetimeFactory<S, R> {

    @Nullable
    private S source;
    @Nullable
    private R result;
    @Nullable
    private Function<S, R> factory;

    private final RuntimeException frozenException;

    public OnetimeFactory(Function<S, R> factory, RuntimeException frozenException) {
        this.factory = Objects.requireNonNull(factory, "factory");
        this.frozenException = Objects.requireNonNull(frozenException, "frozenException");
    }

    public OnetimeFactory(Function<S, R> factory) {
        this(factory, new IllegalStateException("Source object can not be reassigned after result has been built."));
    }

    private void freeze() {
        source = null;
        factory = null;
    }

    private void checkNotFrozen() {
        if (factory == null) {
            throw frozenException;
        }
    }

    public void setSource(S source) {
        checkNotFrozen();
        this.source = source;
    }

    public R build() {
        if (result == null) {
            Objects.requireNonNull(source, "Source can not be null at construction time.");
            Objects.requireNonNull(factory, "Factory can not be null at construction time.");
            result = factory.apply(source);
            freeze();
        }
        return Objects.requireNonNull(result);
    }

}
