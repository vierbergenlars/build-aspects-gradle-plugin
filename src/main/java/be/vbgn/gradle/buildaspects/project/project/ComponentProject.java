package be.vbgn.gradle.buildaspects.project.project;

import be.vbgn.gradle.buildaspects.component.Component;
import java.util.Objects;
import org.gradle.api.Project;

public class ComponentProject {

    private final Component component;
    private final Project project;


    ComponentProject(Project project, Component component) {
        this.component = component;
        this.project = project;
    }

    public Project getProject() {
        return project;
    }

    public Component getComponent() {
        return component;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ComponentProject that = (ComponentProject) o;
        return getComponent().equals(that.getComponent()) &&
                getProject().equals(that.getProject());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getComponent(), getProject());
    }

    @Override
    public String toString() {
        return "ComponentProject{" + project + " (" + component + ")}";
    }
}
