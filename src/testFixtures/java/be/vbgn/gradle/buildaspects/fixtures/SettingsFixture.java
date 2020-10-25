package be.vbgn.gradle.buildaspects.fixtures;

import groovy.lang.Closure;
import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nullable;
import org.gradle.StartParameter;
import org.gradle.api.Action;
import org.gradle.api.UnknownProjectException;
import org.gradle.api.initialization.ConfigurableIncludedBuild;
import org.gradle.api.initialization.ProjectDescriptor;
import org.gradle.api.initialization.Settings;
import org.gradle.api.initialization.dsl.ScriptHandler;
import org.gradle.api.invocation.Gradle;
import org.gradle.api.plugins.ExtensionContainer;
import org.gradle.api.plugins.ObjectConfigurationAction;
import org.gradle.api.plugins.PluginContainer;
import org.gradle.api.plugins.PluginManager;
import org.gradle.caching.configuration.BuildCacheConfiguration;
import org.gradle.plugin.management.PluginManagementSpec;
import org.gradle.vcs.SourceControl;
import org.mockito.Mockito;

public class SettingsFixture implements Settings {

    final Set<ProjectDescriptor> projectDescriptorSet = new HashSet<>();
    final private ExtensionContainer extensionContainer = new ExtensionContainerFixture();

    private final Gradle gradle = Mockito.mock(Gradle.class, (invocation) -> {
        throw new UnsupportedOperationException();
    });

    public SettingsFixture() {
        projectDescriptorSet.add(new ProjectDescriptorFixture("", "root project", null, this));
        Mockito.doNothing().when(gradle).allprojects(Mockito.any());
    }

    private ProjectDescriptor createOrGetProjectDescriptor(String path) {
        ProjectDescriptor existingProjectDescriptor = findProject(path);
        if (existingProjectDescriptor != null) {
            return existingProjectDescriptor;
        }
        String[] parts = path.split(":");
        String parentPath = String.join(":", Arrays.copyOfRange(parts, 0, parts.length - 1));
        ProjectDescriptor parentProject = createOrGetProjectDescriptor(parentPath);
        ProjectDescriptor projectDescriptor = new ProjectDescriptorFixture(path, parts[parts.length - 1], parentProject,
                this);
        projectDescriptorSet.add(projectDescriptor);
        return projectDescriptor;
    }

    @Override
    public void include(String... strings) {
        for (String path : strings) {
            createOrGetProjectDescriptor(path);
        }

    }

    @Override
    public void includeFlat(String... strings) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Settings getSettings() {
        return this;
    }

    @Override
    public ScriptHandler getBuildscript() {
        throw new UnsupportedOperationException();
    }

    @Override
    public File getSettingsDir() {
        throw new UnsupportedOperationException();
    }

    @Override
    public File getRootDir() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ProjectDescriptor getRootProject() {
        return project(":");
    }

    @Override
    public ProjectDescriptor project(String s) throws UnknownProjectException {
        ProjectDescriptor projectDescriptor = findProject(s);
        if (projectDescriptor == null) {
            throw new UnknownProjectException("Project " + s + " does not exist.");
        }
        return projectDescriptor;
    }

    @Nullable
    @Override
    public ProjectDescriptor findProject(String s) {
        return projectDescriptorSet.stream()
                .filter(projectDescriptor -> Objects.equals(s, projectDescriptor.getPath()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public ProjectDescriptor project(File file) throws UnknownProjectException {
        throw new UnsupportedOperationException();
    }

    @Nullable
    @Override
    public ProjectDescriptor findProject(File file) {
        throw new UnsupportedOperationException();
    }

    @Override
    public StartParameter getStartParameter() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Gradle getGradle() {
        return gradle;
    }

    @Override
    public void includeBuild(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void includeBuild(Object o, Action<ConfigurableIncludedBuild> action) {
        throw new UnsupportedOperationException();
    }

    @Override
    public BuildCacheConfiguration getBuildCache() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void buildCache(Action<? super BuildCacheConfiguration> action) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void pluginManagement(Action<? super PluginManagementSpec> action) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PluginManagementSpec getPluginManagement() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sourceControl(Action<? super SourceControl> action) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SourceControl getSourceControl() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void enableFeaturePreview(String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ExtensionContainer getExtensions() {
        return extensionContainer;
    }

    @Override
    public PluginContainer getPlugins() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void apply(Closure closure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void apply(Action<? super ObjectConfigurationAction> action) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void apply(Map<String, ?> map) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PluginManager getPluginManager() {
        throw new UnsupportedOperationException();
    }
}
