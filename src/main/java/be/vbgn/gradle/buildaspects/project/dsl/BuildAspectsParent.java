package be.vbgn.gradle.buildaspects.project.dsl;

import be.vbgn.gradle.buildaspects.component.Component;
import be.vbgn.gradle.buildaspects.project.project.ComponentProject;
import java.util.List;
import java.util.function.Predicate;
import javax.inject.Inject;
import org.gradle.api.Action;
import org.gradle.api.NonNullApi;
import org.gradle.api.Project;

@NonNullApi
public class BuildAspectsParent implements BuildAspects {

    private final Project project;
    private final List<? extends ComponentProject> componentProjects;

    @Inject
    public BuildAspectsParent(Project project, List<? extends ComponentProject> componentProjects) {
        this.project = project;
        this.componentProjects = componentProjects;
    }

    private void componentProjects(Action<? super ComponentProject> configure) {
        project.subprojects(project1 -> {
            componentProjects.stream()
                    .filter(cp -> cp.getProject().equals(project1))
                    .forEach(configure::execute);
        });
    }

    @Override
    public void when(Predicate<? super Component> filter, Action<? super Project> configure) {
        componentProjects(componentProject -> {
            if (filter.test(componentProject.getComponent())) {
                configure.execute(componentProject.getProject());
            }
        });
    }

}
