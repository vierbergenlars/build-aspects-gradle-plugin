package be.vbgn.gradle.buildaspects.aspect;

public interface WritableAspect<T> extends Aspect<T> {

    WritableAspect<T> add(T option);
}
