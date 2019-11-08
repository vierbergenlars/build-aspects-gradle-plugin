package be.vbgn.gradle.buildaspects.aspect;

import java.util.Objects;
import javax.annotation.Nullable;
import org.gradle.api.Named;

public class Property<T> implements Named {

    private final String name;

    private final T value;

    Property(String name, T value) {
        this.name = name;
        this.value = value;
    }


    @Override
    public String getName() {
        return name;
    }

    @Nullable
    public T getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Property<?> property = (Property<?>) o;
        return getName().equals(property.getName()) &&
                Objects.equals(getValue(), property.getValue());
    }

    @Override
    public String toString() {
        return "Property{" +
                "name='" + name + '\'' +
                ", value=" + value +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getValue());
    }
}
