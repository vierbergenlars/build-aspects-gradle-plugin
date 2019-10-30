package be.vbgn.gradle.buildaspects.aspect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ComponentBuilder {
    private final List<Component> components;

    public ComponentBuilder() {
        this(Collections.singletonList(new Component(Collections.emptyList())));
    }

    private ComponentBuilder(List<Component> components) {
        this.components = Collections.unmodifiableList(components);
    }


    public ComponentBuilder addAspect(Aspect<?> aspect) {
        List<Component> components = new ArrayList<>(this.components.size()*aspect.getProperties().size());
        for (Component component : this.components) {
            for (Property<?> property : aspect.getProperties()) {
                components.add(component.withProperty(property));
            }
        }

        return new ComponentBuilder(components);
    }

    public List<Component> build() {
        return components;
    }

}
