package be.vbgn.gradle.buildaspects.project.project;

import be.vbgn.gradle.buildaspects.settings.project.VariantProjectDescriptor;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.gradle.api.Project;

public class VariantProjectFactory {

    private final Set<? extends VariantProjectDescriptor> variantProjectDescriptors;

    private final Map<Project, VariantProject> variantProjectCache;

    public VariantProjectFactory(Set<? extends VariantProjectDescriptor> variantProjectDescriptors) {
        this.variantProjectDescriptors = variantProjectDescriptors;
        variantProjectCache = new HashMap<>(variantProjectDescriptors.size());
    }

    private VariantProject createVariantProject(Project project, VariantProjectDescriptor descriptor) {
        assert project.getPath().equals(descriptor.getProjectDescriptor().getPath());
        variantProjectCache.computeIfAbsent(project, p -> new VariantProject(p, descriptor.getVariant()));
        return variantProjectCache.get(project);
    }

    public Optional<VariantProject> createVariantProject(Project project) {
        for (VariantProjectDescriptor variantProjectDescriptor : variantProjectDescriptors) {
            if (variantProjectDescriptor.getProjectDescriptor().getPath().equals(project.getPath())) {
                return Optional.of(createVariantProject(project, variantProjectDescriptor));
            }
        }
        return Optional.empty();
    }

    public Set<VariantProject> createVariantProjectsForParent(Project parentProject) {
        Set<VariantProject> childVariantProjects = new HashSet<>(parentProject.getChildProjects().size());
        for (VariantProjectDescriptor variantProjectDescriptor : variantProjectDescriptors) {
            if (variantProjectDescriptor.getParentProjectDescriptor().getPath().equals(parentProject.getPath())) {
                Project childProject = parentProject
                        .project(variantProjectDescriptor.getProjectDescriptor().getPath());
                childVariantProjects.add(createVariantProject(childProject, variantProjectDescriptor));
            }
        }
        return Collections.unmodifiableSet(childVariantProjects);
    }
}
