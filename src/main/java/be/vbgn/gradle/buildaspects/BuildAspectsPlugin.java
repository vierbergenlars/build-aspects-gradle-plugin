/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package be.vbgn.gradle.buildaspects;

import be.vbgn.gradle.buildaspects.internal.PluginManager;
import be.vbgn.gradle.buildaspects.plugins.PluginAware;
import be.vbgn.gradle.buildaspects.project.dsl.BuildAspectsLeaf;
import be.vbgn.gradle.buildaspects.project.dsl.BuildAspectsParent;
import be.vbgn.gradle.buildaspects.project.dsl.ProjectExtension;
import be.vbgn.gradle.buildaspects.project.project.VariantProject;
import be.vbgn.gradle.buildaspects.project.project.VariantProjectFactory;
import be.vbgn.gradle.buildaspects.project.project.VariantProjectFactoryImpl;
import be.vbgn.gradle.buildaspects.settings.dsl.BuildAspects;
import be.vbgn.gradle.buildaspects.settings.dsl.BuildAspectsRoot;
import be.vbgn.gradle.buildaspects.settings.dsl.BuildAspectsRootImpl;
import be.vbgn.gradle.buildaspects.variant.GroovyBuildVariant;
import java.util.Set;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.initialization.Settings;

public class BuildAspectsPlugin implements Plugin<Object>, PluginAware<BuildAspects> {

    private static final String BUILD_ASPECTS_EXTENSION = "buildAspects";
    private static final String BUILD_VARIANT_EXTENSION = "buildVariant";
    private static final String BUILD_ASPECTS_CONVENTION = "buildAspects-convention";

    private PluginManager<BuildAspects> pluginManager = null;

    public void apply(Settings settings) {
        pluginManager = new PluginManager<>();
        BuildAspectsRoot buildAspects = settings.getExtensions()
                .create(BuildAspectsRoot.class, BUILD_ASPECTS_EXTENSION, BuildAspectsRootImpl.class, settings,
                        pluginManager);
        pluginManager.apply(buildAspects);
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
                                .add(BUILD_VARIANT_EXTENSION, new GroovyBuildVariant(vp.getVariant()));
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

    public void applyPlugin(Plugin<BuildAspects> buildAspectsPlugin) {
        if (pluginManager == null) {
            throw new IllegalStateException(
                    "Build aspects plugins can not be applied before the plugin is applied to Settings, or if the plugin is applied to a Project.");
        }
        pluginManager.applyPlugin(buildAspectsPlugin);
    }

    public <T extends Plugin<BuildAspects>> T applyPlugin(Class<T> buildAspectsPlugin) {
        if (pluginManager == null) {
            throw new IllegalStateException(
                    "Build aspects plugins can not be applied before the plugin is applied to Settings, or if the plugin is applied to a Project.");
        }
        return pluginManager.applyPlugin(buildAspectsPlugin);
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
