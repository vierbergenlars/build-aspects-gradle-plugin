package be.vbgn.gradle.buildaspects.project.dsl;

import be.vbgn.gradle.buildaspects.project.project.VariantProject;
import be.vbgn.gradle.buildaspects.project.project.VariantProjectFactory;
import be.vbgn.gradle.buildaspects.variant.Variant;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import javax.inject.Inject;
import org.gradle.api.Project;
import org.gradle.api.UnknownProjectException;

public class ProjectExtension {
    private final Project thisProject;
    private final VariantProjectFactory variantProjectFactory;

    @Inject
    public ProjectExtension(Project thisProject, VariantProjectFactory variantProjectFactory) {
        this.thisProject = thisProject;
        this.variantProjectFactory = variantProjectFactory;
    }

    @Nullable
    public Project findProject(String baseProject, Variant variant) {
        Project parentProject = thisProject.findProject(baseProject);
        if(parentProject == null) {
            return null;
        }

        Set<Project> foundProjects = variantProjectFactory.createVariantProjectsForParent(parentProject)
                .stream()
                .filter(variantProject -> variantProject.getVariant().getProperties().equals(variant.getProperties()))
                .map(VariantProject::getProject)
                .collect(Collectors.toSet());

        switch (foundProjects.size()) {
            case 0:
                return null;
            case 1:
                return foundProjects.iterator().next();
            default:
                throw new IllegalStateException("Multiple projects found for parent "+parentProject+" and "+variant);
        }
    }

    public Project project(String baseProject, Variant variant) {
        Project project = findProject(baseProject, variant);
        if(project == null) {
            throw new UnknownProjectException(String.format("Project with path '%s' and variant '%s' could not be found in %s.", baseProject, variant, this));
        }
        return project;
    }

    public String variantTask(String baseProject, Variant variant, String taskName) {
        return project(baseProject, variant).getPath()+":"+taskName;
    }
}
