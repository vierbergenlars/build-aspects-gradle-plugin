package be.vbgn.gradle.buildaspects.project.dsl;

import be.vbgn.gradle.buildaspects.variant.Variant;
import java.util.function.Predicate;
import javax.inject.Inject;
import org.gradle.api.Action;
import org.gradle.api.Project;

public class BuildAspectsLeaf implements BuildAspects {

    private final Project project;
    private final Variant variant;

    @Inject
    public BuildAspectsLeaf(Project project, Variant variant) {
        this.project = project;
        this.variant = variant;
    }

    @Override
    public void when(Predicate<? super Variant> filter, Action<? super Project> configure) {
        if (filter.test(variant)) {
            configure.execute(project);
        }
    }
}
