package be.vbgn.gradle.buildaspects.settings.project;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.gradle.api.Namer;

public class DefaultVariantProjectNamer implements Namer<ParentVariantProjectDescriptor> {

    private final List<String> usedPropertyNames = new ArrayList<>();

    public void addUsedProperty(String usedPropertyName) {
        usedPropertyNames.add(usedPropertyName);
    }

    @Override
    public String determineName(ParentVariantProjectDescriptor object) {
        String parentName = object.getParentProjectDescriptor().getName();
        String propertiesName = usedPropertyNames.stream()
                .map(propertyName -> propertyName + "-" + object.getVariant().getProperty(propertyName))
                .collect(Collectors.joining("-"));
        return parentName + "-" + propertiesName;
    }
}
