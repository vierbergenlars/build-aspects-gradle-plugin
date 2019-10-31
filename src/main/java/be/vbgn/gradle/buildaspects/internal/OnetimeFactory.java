package be.vbgn.gradle.buildaspects.internal;

import java.util.Objects;
import java.util.function.Function;
import org.gradle.api.NonNullApi;

@NonNullApi
public class OnetimeFactory<S, R> {
    private S source;
    private R result;
    private Function<S, R> factory;

    public OnetimeFactory(Function<S, R> factory) {
        this.factory = factory;
    }

    private void freeze() {
        source = null;
        factory = null;
    }

    private void checkNotFrozen() {
        if(factory == null) {
            throw new IllegalStateException("Source object can not be reassigned after result has been built.");
        }
    }

    public void setSource(S source) {
        checkNotFrozen();
        this.source = source;
    }

    public R build() {
        if(result == null) {
            Objects.requireNonNull(source, "Source can not be null at construction time.");
            result = factory.apply(source);
            freeze();
        }
        return result;
    }

}
