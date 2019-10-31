package be.vbgn.gradle.buildaspects.project;

import be.vbgn.gradle.buildaspects.internal.EventDispatcher;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.gradle.api.Action;
import org.gradle.api.UnknownProjectException;
import org.gradle.api.initialization.ProjectDescriptor;
import org.gradle.api.initialization.Settings;

public class ProjectHandler {


    private final Settings settings;
    private final Set<ProjectDescriptor> projectDescriptors = new HashSet<>();
    private final EventDispatcher<ProjectDescriptor> projectAddedDispatcher = new EventDispatcher<>();

    public ProjectHandler(Settings settings) {
        this.settings = settings;
    }

    public Collection<ProjectDescriptor> getProjects() {
        return Collections.unmodifiableCollection(projectDescriptors);
    }

    public void project(String project) throws UnknownProjectException {
        project(settings.project(project));
    }

    public void project(ProjectDescriptor projectDescriptor) {
        projectDescriptors.add(projectDescriptor);
        projectAddedDispatcher.fire(projectDescriptor);
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
