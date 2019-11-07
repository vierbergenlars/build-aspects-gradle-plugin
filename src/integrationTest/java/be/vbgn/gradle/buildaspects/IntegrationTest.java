package be.vbgn.gradle.buildaspects;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.BuildTask;
import org.junit.Test;

public class IntegrationTest extends AbstractIntegrationTest {

    private final Path integrationTests = Paths
            .get("src/integrationTest/resources/be/vbgn/gradle/buildaspects/integration");

    @Test
    public void settingsPlugin() throws IOException {
        BuildResult buildResult = createGradleRunner(integrationTests.resolve("settingsPlugin"))
                .withArguments("clean")
                .build();

        Set<String> projectPaths = buildResult.getTasks()
                .stream()
                .map(BuildTask::getPath)
                .map(s -> s.substring(0, s.indexOf(":clean")))
                .collect(Collectors.toSet());

        assertEquals(new HashSet<>(Arrays.asList(
                "",
                ":settingsPlugin-systemVersion-1.0-communityEdition-true",
                ":settingsPlugin-systemVersion-2.0-communityEdition-true",
                ":settingsPlugin-systemVersion-1.0-communityEdition-false",
                ":settingsPlugin-systemVersion-2.0-communityEdition-false"
        )), projectPaths);
    }

    @Test
    public void subprojects() throws IOException {

        BuildResult buildResult = createGradleRunner(integrationTests.resolve("subprojects"))
                .withArguments("clean", "--stacktrace")
                .build();

        Set<String> projectPaths = buildResult.getTasks()
                .stream()
                .map(BuildTask::getPath)
                .collect(Collectors.toSet());
        assertEquals(new HashSet<>(Arrays.asList(
                ":clean",
                ":moduleA:clean",
                ":systemB:clean",
                ":systemB:moduleB:clean",
                ":moduleA:moduleA-systemVersion-1.0:clean",
                ":moduleA:moduleA-systemVersion-1.0:moduleASystemVersion1",
                ":moduleA:moduleA-systemVersion-2.0:clean",
                ":systemB:moduleB:moduleB-systemVersion-1.0:clean",
                ":systemB:moduleB:moduleB-systemVersion-2.0:clean",
                ":systemB:moduleB:unmapped-subproject:clean"
        )), projectPaths);
    }

}
