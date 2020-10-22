package be.vbgn.gradle.buildaspects.variant;

import be.vbgn.gradle.buildaspects.aspect.Property;
import java.util.List;

public interface Variant {

    List<Property<?>> getProperties();

    <T> T getProperty(String name);

    default <T> T getProperty(String name, Class<T> clazz) {
        Object prop = getProperty(name);
        if (!clazz.isInstance(prop)) {
            throw NoSuchPropertyException.forClassTypeMismatch(name, prop.getClass(), clazz);
        }
        return (T) prop;
    }
}
