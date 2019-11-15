package be.vbgn.gradle.buildaspects.project.dsl;

import be.vbgn.gradle.buildaspects.project.project.VariantProject;
import be.vbgn.gradle.buildaspects.variant.Variant;
import java.util.Set;
import java.util.function.Predicate;
import javax.inject.Inject;
import org.gradle.api.Action;
import org.gradle.api.Project;

public class BuildAspectsParent implements BuildAspects {

    private final Project project;
    private final Set<? extends VariantProject> variantProjects;

    @Inject
    public BuildAspectsParent(Project project, Set<? extends VariantProject> variantProjects) {
        this.project = project;
        this.variantProjects = variantProjects;
    }

    private void variantProjects(Action<? super VariantProject> configure) {
        project.subprojects(project1 -> variantProjects.stream()
                .filter(vp -> vp.getProject().equals(project1))
                .forEach(configure::execute));
    }

    @Override
    public void when(Predicate<? super Variant> filter, Action<? super Project> configure) {
        variantProjects(variantProject -> {
            if (filter.test(variantProject.getVariant())) {
                configure.execute(variantProject.getProject());
            }
        });
    }

}
