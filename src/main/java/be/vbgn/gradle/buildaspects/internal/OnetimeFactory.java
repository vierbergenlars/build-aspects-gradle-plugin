package be.vbgn.gradle.buildaspects.internal;

import java.util.Objects;
import java.util.function.Function;
import javax.annotation.Nullable;
import org.gradle.api.NonNullApi;

@NonNullApi
public class OnetimeFactory<S, R> {

    @Nullable
    private S source;
    @Nullable
    private R result;
    @Nullable
    private Function<S, R> factory;

    public OnetimeFactory(Function<S, R> factory) {
        this.factory = Objects.requireNonNull(factory, "factory");
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
            Objects.requireNonNull(factory, "Factory can not be null at construction time.");
            result = factory.apply(source);
            freeze();
        }
        return result;
    }

}
