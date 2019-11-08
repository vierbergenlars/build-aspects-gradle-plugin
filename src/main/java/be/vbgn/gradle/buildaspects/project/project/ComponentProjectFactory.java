package be.vbgn.gradle.buildaspects.project.project;

import be.vbgn.gradle.buildaspects.settings.project.ComponentProjectDescriptor;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.gradle.api.Project;

public class ComponentProjectFactory {

    private final Set<? extends ComponentProjectDescriptor> componentProjectDescriptors;

    private final Map<Project, ComponentProject> componentProjectCache;

    public ComponentProjectFactory(Set<? extends ComponentProjectDescriptor> componentProjectDescriptors) {
        this.componentProjectDescriptors = componentProjectDescriptors;
        componentProjectCache = new HashMap<>(componentProjectDescriptors.size());
    }

    private ComponentProject createComponentProject(Project project, ComponentProjectDescriptor descriptor) {
        assert project.getPath().equals(descriptor.getProjectDescriptor().getPath());
        componentProjectCache.computeIfAbsent(project, p -> new ComponentProject(p, descriptor.getComponent()));
        return componentProjectCache.get(project);
    }

    public Optional<ComponentProject> createComponentProject(Project project) {
        for (ComponentProjectDescriptor componentProjectDescriptor : componentProjectDescriptors) {
            if (componentProjectDescriptor.getProjectDescriptor().getPath().equals(project.getPath())) {
                return Optional.of(createComponentProject(project, componentProjectDescriptor));
            }
        }
        return Optional.empty();
    }

    public Set<ComponentProject> createComponentProjectsForParent(Project parentProject) {
        Set<ComponentProject> childComponentProjects = new HashSet<>(parentProject.getChildProjects().size());
        for (ComponentProjectDescriptor componentProjectDescriptor : componentProjectDescriptors) {
            if (componentProjectDescriptor.getParentProjectDescriptor().getPath().equals(parentProject.getPath())) {
                Project childProject = parentProject
                        .project(componentProjectDescriptor.getProjectDescriptor().getPath());
                childComponentProjects.add(createComponentProject(childProject, componentProjectDescriptor));
            }
        }
        return Collections.unmodifiableSet(childComponentProjects);
    }
}
