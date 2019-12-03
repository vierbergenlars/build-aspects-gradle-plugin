package be.vbgn.gradle.buildaspects.variant;

import be.vbgn.gradle.buildaspects.aspect.Property;
import be.vbgn.gradle.buildaspects.variant.NoSuchPropertyException;
import be.vbgn.gradle.buildaspects.variant.Variant;
import groovy.lang.GroovyObjectSupport;
import groovy.lang.MissingPropertyException;
import groovy.lang.ReadOnlyPropertyException;
import java.util.List;
import javax.annotation.Nullable;
import javax.inject.Inject;

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
        try {
            return variant.getProperty(name);
        } catch (NoSuchPropertyException e) {
            throw new MissingPropertyException(name, getClass());
        }
    }

    @Override
    public void setProperty(String property, Object newValue) {
        throw new ReadOnlyPropertyException(property, this.getClass());
    }
}
