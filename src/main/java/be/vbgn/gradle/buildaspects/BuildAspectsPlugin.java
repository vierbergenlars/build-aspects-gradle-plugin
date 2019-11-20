/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package be.vbgn.gradle.buildaspects;

import be.vbgn.gradle.buildaspects.project.dsl.BuildAspectsLeaf;
import be.vbgn.gradle.buildaspects.project.dsl.BuildAspectsParent;
import be.vbgn.gradle.buildaspects.project.dsl.BuildVariant;
import be.vbgn.gradle.buildaspects.project.dsl.ProjectExtension;
import be.vbgn.gradle.buildaspects.project.project.VariantProject;
import be.vbgn.gradle.buildaspects.project.project.VariantProjectFactory;
import be.vbgn.gradle.buildaspects.project.project.VariantProjectFactoryImpl;
import be.vbgn.gradle.buildaspects.settings.dsl.BuildAspects;
import be.vbgn.gradle.buildaspects.settings.dsl.BuildAspectsRoot;
import java.util.Set;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.initialization.Settings;

public class BuildAspectsPlugin implements Plugin<Object> {

    private static final String BUILD_ASPECTS_EXTENSION = "buildAspects";
    private static final String BUILD_VARIANT_EXTENSION = "buildVariant";
    private static final String BUILD_ASPECTS_CONVENTION = "buildAspects-convention";

    public void apply(Settings settings) {
        BuildAspects buildAspects = settings.getExtensions()
                .create(BUILD_ASPECTS_EXTENSION, BuildAspectsRoot.class, settings);
        VariantProjectFactory variantProjectFactory = new VariantProjectFactoryImpl(
                buildAspects.getVariantProjects());
        settings.getGradle().allprojects(project -> {
            // Add overload to findProject() and project() methods
            project.getConvention().getPlugins()
                    .put(BUILD_ASPECTS_CONVENTION, new ProjectExtension(project, variantProjectFactory));
            // Applies if project is a leaf project
            variantProjectFactory.createVariantProject(project)
                    .ifPresent(vp -> {
                        project.getExtensions()
                                .add(BUILD_VARIANT_EXTENSION, new BuildVariant(vp.getVariant()));
                        project.getExtensions()
                                .create(BUILD_ASPECTS_EXTENSION, BuildAspectsLeaf.class, project, vp.getVariant());
                        project.getPluginManager().apply(getClass());
                    });

            // Applies if project has subprojects that are variants
            Set<VariantProject> variantProjects = variantProjectFactory.createVariantProjectsForParent(project);
            if (!variantProjects.isEmpty()) {
                project.getExtensions()
                        .create(BUILD_ASPECTS_EXTENSION, BuildAspectsParent.class, project, variantProjects);
                project.getPluginManager().apply(getClass());
            }

        });
    }

    public void apply(Project project) {
        if (project.getExtensions().findByName(BUILD_ASPECTS_EXTENSION) == null) {
            throw new IllegalStateException("This plugin can not be applied to a project manually.");
        }

    }

    @Override
    public void apply(Object target) {
        if (target instanceof Settings) {
            apply((Settings) target);
        } else if (target instanceof Project) {
            apply((Project) target);
        } else {
            throw new IllegalArgumentException("The plugin can only be applied to Settings and Project.");
        }
    }
}
