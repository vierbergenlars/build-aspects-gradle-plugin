package be.vbgn.gradle.buildaspects.project.dsl;

import be.vbgn.gradle.buildaspects.aspect.Property;
import be.vbgn.gradle.buildaspects.component.Component;
import java.util.List;
import javax.annotation.Nullable;
import javax.inject.Inject;
import org.gradle.api.NonNullApi;

@NonNullApi
public class BuildComponents implements Component {

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
        return component.getProperty(name);
    }
}
