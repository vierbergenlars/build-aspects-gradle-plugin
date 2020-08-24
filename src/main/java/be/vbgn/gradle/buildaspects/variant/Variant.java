package be.vbgn.gradle.buildaspects.variant;

import be.vbgn.gradle.buildaspects.aspect.Property;
import java.util.List;

public interface Variant {

    List<Property<?>> getProperties();

    <T> T getProperty(String name);
}
