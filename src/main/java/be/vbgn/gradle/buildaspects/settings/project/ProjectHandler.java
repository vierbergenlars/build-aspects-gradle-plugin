package be.vbgn.gradle.buildaspects.settings.project;

import be.vbgn.gradle.buildaspects.internal.EventDispatcher;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;
import org.gradle.api.Action;
import org.gradle.api.initialization.ProjectDescriptor;
import org.gradle.api.initialization.Settings;

public class ProjectHandler {


    private final Settings settings;
    private final Set<ProjectDescriptor> projectDescriptors = new HashSet<>();
    private final EventDispatcher<ProjectDescriptor> projectAddedDispatcher = new EventDispatcher<>();
    private final EventDispatcher<ProjectDescriptor> beforeProjectAddedDispatcher = new EventDispatcher<>();

    @Inject
    public ProjectHandler(Settings settings) {
        this.settings = settings;
    }

    public Collection<ProjectDescriptor> getProjects() {
        return Collections.unmodifiableCollection(projectDescriptors);
    }

    public void project(String project) {
        project(settings.project(project));
    }

    public void project(ProjectDescriptor projectDescriptor) {
        beforeProjectAddedDispatcher.fire(projectDescriptor);
        if (!projectDescriptors.add(projectDescriptor)) {
            throw DuplicateProjectException.forProject(projectDescriptor);
        }
        projectAddedDispatcher.fire(projectDescriptor);
    }

    public void beforeProjectAdded(Action<ProjectDescriptor> eventListener) {
        beforeProjectAddedDispatcher.addListener(eventListener);
    }

    public void projectAdded(Action<ProjectDescriptor> eventListener) {
        projectAddedDispatcher.addListener(eventListener);
    }

    public void include(String... projects) {
        settings.include(projects);
        for (String project : projects) {
            project(project);
        }
    }

}
