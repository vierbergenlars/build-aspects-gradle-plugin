package be.vbgn.gradle.buildaspects.project;

import be.vbgn.gradle.buildaspects.aspect.Component;
import org.gradle.api.initialization.ProjectDescriptor;

public class ComponentProject extends ComponentProjectDescriptor {

    private final ProjectDescriptor projectDescriptor;

    ComponentProject(ProjectDescriptor parentProjectDescriptor,
            Component component, ProjectDescriptor projectDescriptor) {
        super(parentProjectDescriptor, component);
        this.projectDescriptor = projectDescriptor;
    }

    public ProjectDescriptor getProjectDescriptor() {
        return projectDescriptor;
    }
}
