package be.vbgn.gradle.buildaspects.fixtures;

import be.vbgn.gradle.buildaspects.settings.dsl.BuildAspectsRootFixture;
import be.vbgn.gradle.buildaspects.settings.dsl.BuildAspectsRootImpl;
import java.util.HashSet;
import java.util.Set;
import org.gradle.api.reflect.ObjectInstantiationException;
import org.gradle.internal.reflect.DirectInstantiator;
import org.gradle.internal.reflect.Instantiator;

public class InstantiatorFixture implements Instantiator {

    static final Instantiator INSTANCE = new InstantiatorFixture();

    private final static Set<Class<?>> directInstantiatorAllowList = new HashSet<>();

    @Override
    public <T> T newInstance(Class<? extends T> aClass, Object... params) throws ObjectInstantiationException {
        if (aClass.equals(BuildAspectsRootImpl.class)) {
            return (T) DirectInstantiator.instantiate(BuildAspectsRootFixture.class, params);
        }
        if (directInstantiatorAllowList.contains(aClass)) {
            return (T) DirectInstantiator.instantiate(aClass, params);
        }
        throw new UnsupportedOperationException();
    }

    public static void allowInstantiationFor(Class<?> aClass) {
        directInstantiatorAllowList.add(aClass);
    }
}
