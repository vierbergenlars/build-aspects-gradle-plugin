package be.vbgn.gradle.buildaspects.component;

import be.vbgn.gradle.buildaspects.aspect.Aspect;
import be.vbgn.gradle.buildaspects.aspect.Property;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ComponentBuilder {

    private ComponentBuilderInternal builder = new ComponentBuilderInternal();

    public void addAspect(Aspect<?> aspect) {
        builder = builder.addAspect(aspect);
    }

    public Collection<? extends Component> getComponents() {
        return builder.build();
    }

    private static class ComponentBuilderInternal {

        private final List<ComponentImpl> components;

        public ComponentBuilderInternal() {
            this(Collections.singletonList(new ComponentImpl(Collections.emptyList())));
        }

        private ComponentBuilderInternal(List<ComponentImpl> components) {
            this.components = Collections.unmodifiableList(components);
        }


        public ComponentBuilderInternal addAspect(Aspect<?> aspect) {
            List<ComponentImpl> components = new ArrayList<>(this.components.size() * aspect.getProperties().size());
            for (ComponentImpl component : this.components) {
                for (Property<?> property : aspect.getProperties()) {
                    components.add(component.withProperty(property));
                }
            }

            return new ComponentBuilderInternal(components);
        }

        public List<? extends Component> build() {
            return components;
        }

    }
}
