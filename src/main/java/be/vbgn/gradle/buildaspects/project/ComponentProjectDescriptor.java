package be.vbgn.gradle.buildaspects.project;

import be.vbgn.gradle.buildaspects.component.Component;
import org.gradle.api.initialization.ProjectDescriptor;

public class ComponentProjectDescriptor {

    private final ProjectDescriptor parentProjectDescriptor;
    private final Component component;

    ComponentProjectDescriptor(ProjectDescriptor parentProjectDescriptor, Component component) {
        this.parentProjectDescriptor = parentProjectDescriptor;
        this.component = component;
    }

    public Component getComponent() {
        return component;
    }

    public ProjectDescriptor getParentProjectDescriptor() {
        return parentProjectDescriptor;
    }
}
