package be.vbgn.gradle.buildaspects.project.project;

import be.vbgn.gradle.buildaspects.component.Component;
import org.gradle.api.Project;

public class ComponentProject {

    private final Component component;
    private final Project project;


    public ComponentProject(Project project, Component component) {
        this.component = component;
        this.project = project;
    }

    public Project getProject() {
        return project;
    }

    public Component getComponent() {
        return component;
    }
}
