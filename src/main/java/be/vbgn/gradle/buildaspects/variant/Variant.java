package be.vbgn.gradle.buildaspects.variant;

import be.vbgn.gradle.buildaspects.aspect.Property;
import java.util.List;
import javax.annotation.Nullable;

public interface Variant {

    List<Property<?>> getProperties();

    @Nullable
    Object getProperty(String name);
}
