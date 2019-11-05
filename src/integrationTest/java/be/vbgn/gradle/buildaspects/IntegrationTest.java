package be.vbgn.gradle.buildaspects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
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
                .withArguments("clean")
                .build();

        Set<String> projectPaths = buildResult.getTasks()
                .stream()
                .map(BuildTask::getPath)
                .map(s -> s.substring(0, s.indexOf(":clean")))
                .collect(Collectors.toSet());
        assertEquals(new HashSet<>(Arrays.asList(
                "",
                ":moduleA:moduleA-systemVersion-1.0",
                ":moduleA:moduleA-systemVersion-2.0",
                ":systemB:moduleB:moduleB-systemVersion-1.0",
                ":systemB:moduleB:moduleB-systemVersion-2.0"
        )), projectPaths);
    }

}
