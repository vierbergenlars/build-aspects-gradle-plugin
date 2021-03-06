/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package be.vbgn.gradle.buildaspects;

import static org.gradle.internal.impldep.org.junit.Assert.assertEquals;

import be.vbgn.gradle.buildaspects.fixtures.SettingsFixture;
import be.vbgn.gradle.buildaspects.settings.dsl.BuildAspects;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.gradle.api.Project;
import org.gradle.api.initialization.ProjectDescriptor;
import org.gradle.api.initialization.Settings;
import org.gradle.api.internal.plugins.PluginApplicationException;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Test;

public class BuildAspectsPluginTest {

    @Test(expected = PluginApplicationException.class)
    public void throwsWhenAppliedOnProject() {
        Project project = ProjectBuilder.builder().build();
        project.getPluginManager().apply(BuildAspectsPlugin.class);
    }

    @Test
    public void appliesToSettingsFixture() {
        Settings settings = new SettingsFixture();
        BuildAspectsPlugin buildAspectsPlugin = new BuildAspectsPlugin();
        buildAspectsPlugin.apply(settings);

        BuildAspects buildAspects = settings.getExtensions().getByType(BuildAspects.class);

        buildAspects.getAspects().create("aspect", 1, 2);

        buildAspects.getProjects().include(":project");

        Set<String> projects = settings.project(":project").getChildren().stream().map(ProjectDescriptor::getPath)
                .collect(
                        Collectors.toSet());

        assertEquals(new HashSet<>(Arrays.asList(":project:project-aspect-1", ":project:project-aspect-2")), projects);
    }
}
