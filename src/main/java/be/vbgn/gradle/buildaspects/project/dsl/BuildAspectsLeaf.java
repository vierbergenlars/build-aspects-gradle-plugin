package be.vbgn.gradle.buildaspects.project.dsl;

import be.vbgn.gradle.buildaspects.component.Component;
import java.util.function.Predicate;
import javax.inject.Inject;
import org.gradle.api.Action;
import org.gradle.api.Project;

public class BuildAspectsLeaf implements BuildAspects {

    private final Project project;
    private final Component component;

    @Inject
    public BuildAspectsLeaf(Project project, Component component) {
        this.project = project;
        this.component = component;
    }

    @Override
    public void when(Predicate<? super Component> filter, Action<? super Project> configure) {
        if (filter.test(component)) {
            configure.execute(project);
        }
    }
}
