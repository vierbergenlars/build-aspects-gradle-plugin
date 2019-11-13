package be.vbgn.gradle.buildaspects.settings.project;

import be.vbgn.gradle.buildaspects.variant.Variant;
import org.gradle.api.Project;
import org.gradle.api.initialization.ProjectDescriptor;

public class VariantProjectDescriptor extends ParentVariantProjectDescriptor {

    private final ProjectDescriptor projectDescriptor;

    VariantProjectDescriptor(ProjectDescriptor parentProjectDescriptor,
            Variant variant, ProjectDescriptor projectDescriptor) {
        super(parentProjectDescriptor, variant);
        this.projectDescriptor = projectDescriptor;
    }

    public ProjectDescriptor getProjectDescriptor() {
        return projectDescriptor;
    }

    public boolean isForProject(Project project) {
        return projectDescriptor.getPath().equals(project.getPath());
    }
}
