package be.vbgn.gradle.buildaspects.component;

import be.vbgn.gradle.buildaspects.aspect.Property;
import java.util.List;
import javax.annotation.Nullable;

public interface Component {

    List<Property<?>> getProperties();

    @Nullable
    Object getProperty(String name);
}
