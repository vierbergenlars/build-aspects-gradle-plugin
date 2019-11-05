package be.vbgn.gradle.buildaspects.settings.project;

import java.util.stream.Collectors;
import org.gradle.api.Namer;

public class DefaultComponentProjectNamer implements Namer<ParentComponentProjectDescriptor> {

    @Override
    public String determineName(ParentComponentProjectDescriptor object) {
        String parentName = object.getParentProjectDescriptor().getName();
        String propertiesName = object.getComponent().getProperties().stream()
                .map(p -> p.getName() + "-" + p.getValue()).collect(
                        Collectors.joining("-"));
        return parentName + "-" + propertiesName;
    }
}
