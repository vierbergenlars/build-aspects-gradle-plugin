package be.vbgn.gradle.buildaspects;

import static org.junit.Assume.assumeTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public abstract class AbstractIntegrationTest {

    @Parameters(name = "Gradle v{0}: {1}-dsl")
    public static Collection<Object[]> testData() {
        String[] gradleVersions = new String[]{
                "6.4",
                "6.3",
                "6.2.2",
                "6.1.1",
                "6.0.1",
                "5.6.4",
                "5.5.1",
                "5.4.1",
                "5.3.1",
        };
        String[] gradleDsls = new String[]{
                "groovy",
                "kotlin"
        };
        String forceGradleVersion = System.getProperty("be.vbgn.gradle.buildaspects.integration.useGradleVersion");
        if (forceGradleVersion != null) {
            gradleVersions = new String[]{forceGradleVersion};
        }

        List<Object[]> parameters = new ArrayList<>();

        for (String gradleVersion : gradleVersions) {
            for (String dsl : gradleDsls) {
                parameters.add(new Object[]{gradleVersion, dsl});
            }
        }
        return parameters;
    }

    @Parameter(0)
    public String gradleVersion;

    @Parameter(1)
    public String gradleDsl;

    @Rule
    public final TemporaryFolder testProjectDir = new TemporaryFolder();


    protected GradleRunner createGradleRunner(Path projectFolder) throws IOException {
        if(gradleDsl.equals("kotlin")) {
            assumeTrue("Has kotlin DSL variant", projectFolder.resolve("settings.gradle.kts").toFile().exists());
        } else if(gradleDsl.equals("groovy")) {
            assumeTrue("Has groovy DSL variant", projectFolder.resolve("settings.gradle").toFile().exists());
        }

        FileUtils.copyDirectory(projectFolder.toFile(), testProjectDir.getRoot(), file -> {
            if(file.getName().endsWith(".gradle")) {
                return "groovy".equals(gradleDsl);
            } else if(file.getName().endsWith(".gradle.kts")) {
                return "kotlin".equals(gradleDsl);
            } else {
                return true;
            }
        });
        GradleRunner gradleRunner = GradleRunner.create()
                .withProjectDir(testProjectDir.getRoot())
                .withDebug(true)
                .forwardOutput();

        if (System.getProperty("be.vbgn.gradle.buildaspects.integration.forceCurrentGradleVersion") != null) {
            gradleRunner.withGradleVersion(gradleVersion);
        }

        return gradleRunner;

    }

}
