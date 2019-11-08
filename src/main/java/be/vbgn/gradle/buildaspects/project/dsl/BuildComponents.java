package be.vbgn.gradle.buildaspects.project.dsl;

import be.vbgn.gradle.buildaspects.aspect.Property;
import be.vbgn.gradle.buildaspects.component.Component;
import groovy.lang.GroovyObjectSupport;
import groovy.lang.MissingPropertyException;
import groovy.lang.ReadOnlyPropertyException;
import java.util.List;
import javax.annotation.Nullable;
import javax.inject.Inject;

public class BuildComponents extends GroovyObjectSupport implements Component {

    private final Component component;

    @Inject
    public BuildComponents(Component component) {
        this.component = component;
    }

    @Override
    public List<Property<?>> getProperties() {
        return component.getProperties();
    }

    @Nullable
    @Override
    public Object getProperty(String name) {
        if ("properties".equals(name)) {
            return getProperties();
        }
        try {
            return component.getProperty(name);
        } catch (IllegalArgumentException e) {
            throw new MissingPropertyException(name, getClass());
        }
    }

    @Override
    public void setProperty(String property, Object newValue) {
        throw new ReadOnlyPropertyException(property, this.getClass());
    }
}
