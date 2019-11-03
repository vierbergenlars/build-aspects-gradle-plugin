package be.vbgn.gradle.buildaspects.aspect;

import org.gradle.api.NonNullApi;

@NonNullApi
public interface WritableAspect<T> extends Aspect<T> {

    WritableAspect<T> add(T option);
}
