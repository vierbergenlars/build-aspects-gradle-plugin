package be.vbgn.gradle.buildaspects.settings.project;

import be.vbgn.gradle.buildaspects.component.Component;
import org.gradle.api.Project;
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

    public boolean isForProject(Project project) {
        return projectDescriptor.getPath().equals(project.getPath());
    }
}
