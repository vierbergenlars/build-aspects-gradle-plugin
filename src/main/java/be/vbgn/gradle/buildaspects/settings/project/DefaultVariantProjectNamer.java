package be.vbgn.gradle.buildaspects.settings.project;

import java.util.stream.Collectors;
import org.gradle.api.Namer;

public class DefaultVariantProjectNamer implements Namer<ParentVariantProjectDescriptor> {

    @Override
    public String determineName(ParentVariantProjectDescriptor object) {
        String parentName = object.getParentProjectDescriptor().getName();
        String propertiesName = object.getVariant().getProperties().stream()
                .map(p -> p.getName() + "-" + p.getValue()).collect(
                        Collectors.joining("-"));
        return parentName + "-" + propertiesName;
    }
}
