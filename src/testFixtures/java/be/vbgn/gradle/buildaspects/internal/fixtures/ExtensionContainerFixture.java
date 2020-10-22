package be.vbgn.gradle.buildaspects.internal.fixtures;

import be.vbgn.gradle.buildaspects.settings.dsl.BuildAspectsRootFixture;
import be.vbgn.gradle.buildaspects.settings.dsl.BuildAspectsRootImpl;
import org.gradle.api.reflect.ObjectInstantiationException;
import org.gradle.internal.extensibility.DefaultConvention;
import org.gradle.internal.reflect.DirectInstantiator;
import org.gradle.internal.reflect.Instantiator;

public class ExtensionContainerFixture extends DefaultConvention {

    private static class FakeInstantiator implements Instantiator {

        private static final Instantiator INSTANCE = new FakeInstantiator();

        @Override
        public <T> T newInstance(Class<? extends T> aClass, Object... params) throws ObjectInstantiationException {
            if (aClass.equals(BuildAspectsRootImpl.class)) {
                return (T) DirectInstantiator.instantiate(BuildAspectsRootFixture.class, params);
            }
            throw new UnsupportedOperationException();
        }
    }

    public ExtensionContainerFixture() {
        super(FakeInstantiator.INSTANCE);
    }
}
