package be.vbgn.gradle.buildaspects.settings.project;

import be.vbgn.gradle.buildaspects.component.Component;
import org.gradle.api.initialization.ProjectDescriptor;

public class ParentComponentProjectDescriptor {

    private final ProjectDescriptor parentProjectDescriptor;
    private final Component component;

    ParentComponentProjectDescriptor(ProjectDescriptor parentProjectDescriptor, Component component) {
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
