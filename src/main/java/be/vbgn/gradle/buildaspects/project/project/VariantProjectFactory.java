package be.vbgn.gradle.buildaspects.project.project;

import java.util.Optional;
import java.util.Set;
import org.gradle.api.Project;

public interface VariantProjectFactory {

    Optional<VariantProject> createVariantProject(Project project);

    Set<VariantProject> createVariantProjectsForParent(Project parentProject);
}
