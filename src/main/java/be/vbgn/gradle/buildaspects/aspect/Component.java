package be.vbgn.gradle.buildaspects.aspect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Component {

    private List<Property<?>> properties;

    Component(List<Property<?>> properties) {
        this.properties = properties;
    }

    public List<Property<?>> getProperties() {
        return Collections.unmodifiableList(properties);
    }

    public Map<String, ?> toMap() {
        return getProperties()
                .stream()
                .collect(Collectors.toUnmodifiableMap(Property::getName, Property::getValue));
    }

    Component withProperty(Property<?> property) {
        List<Property<?>> properties = new ArrayList<>(this.properties);
        properties.add(property);
        return new Component(properties);
    }
}
