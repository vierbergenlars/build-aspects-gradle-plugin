package be.vbgn.gradle.buildaspects.project;

import java.util.stream.Collectors;
import org.gradle.api.Namer;

public class DefaultComponentProjectNamer implements Namer<ComponentProjectDescriptor> {

    @Override
    public String determineName(ComponentProjectDescriptor object) {
        String parentName = object.getParentProjectDescriptor().getName();
        String propertiesName = object.getComponent().getProperties().stream()
                .map(p -> p.getName() + "-" + p.getValue()).collect(
                        Collectors.joining("-"));
        return parentName + "-" + propertiesName;
    }
}
