package be.vbgn.gradle.buildaspects;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
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

        List<String> taskPaths = buildResult.getTasks()
                .stream()
                .map(BuildTask::getPath)
                .collect(Collectors.toList());

        for(String projectName: Arrays.asList(
                "",
                ":settingsPlugin-systemVersion-1.0-communityEdition-true",
                ":settingsPlugin-systemVersion-2.0-communityEdition-true",
                ":settingsPlugin-systemVersion-1.0-communityEdition-false",
                ":settingsPlugin-systemVersion-2.0-communityEdition-false"
                )) {
            assertTrue(projectName+":clean", taskPaths.contains(projectName+":clean"));
        }
    }

}
