package be.vbgn.gradle.buildaspects.variant;

import be.vbgn.gradle.buildaspects.aspect.Property;
import groovy.lang.GroovyObjectSupport;
import groovy.lang.ReadOnlyPropertyException;
import java.util.List;
import javax.annotation.Nullable;

public class GroovyBuildVariant extends GroovyObjectSupport implements Variant {

    private final Variant variant;

    public GroovyBuildVariant(Variant variant) {
        this.variant = variant;
    }

    @Override
    public List<Property<?>> getProperties() {
        return variant.getProperties();
    }

    @Nullable
    @Override
    public Object getProperty(String name) {
        if ("properties".equals(name)) {
            return getProperties();
        }
        return variant.getProperty(name);
    }

    @Override
    public void setProperty(String property, Object newValue) {
        throw new ReadOnlyPropertyException(property, this.getClass());
    }
}
