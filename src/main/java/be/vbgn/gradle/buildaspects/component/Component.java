package be.vbgn.gradle.buildaspects.component;

import be.vbgn.gradle.buildaspects.aspect.Property;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public class Component {

    private List<Property<?>> properties;

    Component(List<Property<?>> properties) {
        this.properties = properties;
    }

    public List<Property<?>> getProperties() {
        return Collections.unmodifiableList(properties);
    }

    @Nullable
    public Object getProperty(String name) {
        for (Property<?> property : properties) {
            if (property.getName().equals(name)) {
                return property.getValue();
            }
        }
        return null;
    }

    public Map<String, ?> toMap() {
        return Collections.unmodifiableMap(getProperties()
                .stream()
                .collect(Collectors.toMap(Property::getName, Property::getValue)));
    }

    Component withProperty(Property<?> property) {
        List<Property<?>> properties = new ArrayList<>(this.properties);
        properties.add(property);
        return new Component(properties);
    }
}
