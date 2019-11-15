package be.vbgn.gradle.buildaspects.variant;

import be.vbgn.gradle.buildaspects.aspect.Property;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public class VariantImpl implements Variant {

    private final List<? extends Property<?>> properties;

    VariantImpl(List<? extends Property<?>> properties) {
        this.properties = Objects.requireNonNull(properties);
    }

    @Override
    public List<Property<?>> getProperties() {
        return Collections.unmodifiableList(properties);
    }

    @Override
    @Nullable
    public Object getProperty(String name) {
        for (Property<?> property : properties) {
            if (property.getName().equals(name)) {
                return property.getValue();
            }
        }
        throw new IllegalArgumentException("A property with name '" + name + "' does not exist.");
    }

    @Nullable
    public Map<String, ?> toMap() {
        return Collections.unmodifiableMap(getProperties()
                .stream()
                .collect(Collectors.toMap(Property::getName, Property::getValue)));
    }

    VariantImpl withProperty(Property<?> property) {
        List<Property<?>> newProperties = new ArrayList<>(properties);
        newProperties.add(property);
        return new VariantImpl(newProperties);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        VariantImpl variant = (VariantImpl) o;
        return getProperties().equals(variant.getProperties());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getProperties());
    }

    @Override
    public String toString() {
        return "Variant" + toMap();
    }
}
