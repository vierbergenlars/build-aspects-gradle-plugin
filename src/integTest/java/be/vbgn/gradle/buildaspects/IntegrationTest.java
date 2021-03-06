package be.vbgn.gradle.buildaspects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
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

    private final Path integrationTests;

    {
        try {
            integrationTests = Paths.get(getClass().getResource("integration").toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }


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
    public void nestedConfiguration() throws IOException {
        BuildResult buildResult = createGradleRunner(integrationTests.resolve("nestedConfiguration"))
                .withArguments("clean")
                .build();

        Set<String> projectPaths = buildResult.getTasks()
                .stream()
                .map(BuildTask::getPath)
                .map(s -> s.substring(0, s.indexOf(":clean")))
                .collect(Collectors.toSet());

        assertEquals(new HashSet<>(Arrays.asList(
                "",
                ":moduleA",
                ":moduleA:moduleA-systemVersion-1.0-communityEdition-true",
                ":moduleA:moduleA-systemVersion-2.0-communityEdition-true",
                ":moduleA:moduleA-systemVersion-1.0-communityEdition-false",
                ":moduleA:moduleA-systemVersion-2.0-communityEdition-false",
                ":moduleB",
                ":moduleB:moduleB-systemVersion-2.0",
                ":moduleB:moduleB-systemVersion-2.1"
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

    @Test
    public void arbitraryObject() throws IOException {
        BuildResult buildResult = createGradleRunner(integrationTests.resolve("arbitraryObject"))
                .withArguments("clean")
                .build();

        Set<String> projectPaths = buildResult.getTasks()
                .stream()
                .map(BuildTask::getPath)
                .map(s -> s.substring(0, s.indexOf(":clean")))
                .collect(Collectors.toSet());

        assertEquals(new HashSet<>(Arrays.asList(
                "",
                ":arbitraryObject-systemVersion-1.0-1.2-communityEdition-true",
                ":arbitraryObject-systemVersion-1.0-1.3-communityEdition-true",
                ":arbitraryObject-systemVersion-2.0-1.3-communityEdition-true",
                ":arbitraryObject-systemVersion-1.0-1.2-communityEdition-false",
                ":arbitraryObject-systemVersion-1.0-1.3-communityEdition-false",
                ":arbitraryObject-systemVersion-2.0-1.3-communityEdition-false"
        )), projectPaths);
    }

    @Test
    public void customAspectObject() throws IOException {
        BuildResult buildResult = createGradleRunner(integrationTests.resolve("customAspectObject"))
                .withArguments("clean", "--stacktrace")
                .build();

        Set<String> projectPaths = buildResult.getTasks()
                .stream()
                .map(BuildTask::getPath)
                .map(s -> s.substring(0, s.indexOf(":clean")))
                .collect(Collectors.toSet());

        assertEquals(new HashSet<>(Arrays.asList(
                "",
                ":customAspectObject-systemVersion-1.0-1.2",
                ":customAspectObject-systemVersion-1.0-1.3",
                ":customAspectObject-systemVersion-2.0-1.3"
        )), projectPaths);
    }

    @Test
    public void projectNamer() throws IOException {
        BuildResult buildResult = createGradleRunner(integrationTests.resolve("projectNamer"))
                .withArguments("clean")
                .build();

        Set<String> projectPaths = buildResult.getTasks()
                .stream()
                .map(BuildTask::getPath)
                .map(s -> s.substring(0, s.indexOf(":clean")))
                .collect(Collectors.toSet());

        assertEquals(new HashSet<>(Arrays.asList(
                "",
                ":projectNamer-1.0-community",
                ":projectNamer-2.0-community",
                ":projectNamer-1.0-enterprise",
                ":projectNamer-2.0-enterprise"
        )), projectPaths);
    }

    @Test
    public void exclude() throws IOException {
        BuildResult buildResult = createGradleRunner(integrationTests.resolve(
                "exclude"))
                .withArguments("clean")
                .build();

        Set<String> projectPaths = buildResult.getTasks()
                .stream()
                .map(BuildTask::getPath)
                .map(s -> s.substring(0, s.indexOf(":clean")))
                .collect(Collectors.toSet());

        assertEquals(new HashSet<>(Arrays.asList(
                "",
                ":exclude-systemVersion-1.0-communityEdition-false",
                ":exclude-systemVersion-2.0-communityEdition-true"
        )), projectPaths);
    }

    @Test
    public void nestedWhen() throws IOException {

        BuildResult buildResult = createGradleRunner(integrationTests.resolve(
                "nestedWhen"))
                .withArguments("clean")
                .build();

        Set<String> projectPaths = buildResult.getTasks()
                .stream()
                .map(BuildTask::getPath)
                .map(s -> s.substring(0, s.indexOf(":clean")))
                .collect(Collectors.toSet());

        assertEquals(new HashSet<>(Arrays.asList(
                "",
                ":nestedWhen-systemVersion-1.0-communityEdition-true",
                ":nestedWhen-systemVersion-2.0-communityEdition-true",
                ":nestedWhen-systemVersion-1.0-communityEdition-false",
                ":nestedWhen-systemVersion-2.0-communityEdition-false"
        )), projectPaths);

        final String expectedContent = "systemVersion=1.0; communityEdition=true";
        assertTrue("Subproject configuration runs once", buildResult.getOutput().contains(expectedContent));
        assertEquals("Subproject configuration is only run once", buildResult.getOutput().indexOf(expectedContent),
                buildResult.getOutput().lastIndexOf(expectedContent));
    }

}
