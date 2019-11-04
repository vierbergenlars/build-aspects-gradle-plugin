package be.vbgn.gradle.buildaspects;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
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

    @Parameters(name = "Gradle v{0}")
    public static Collection<Object[]> testData() {
        return Arrays.asList(new Object[][]{
                {"5.6.4"},
                {"5.5.1"},
                {"5.4.1"},
                {"5.3.1"},
        });
    }

    @Parameter(0)
    public String gradleVersion;

    @Rule
    public final TemporaryFolder testProjectDir = new TemporaryFolder();

    protected GradleRunner createGradleRunner(Path projectFolder) throws IOException {
        FileUtils.copyDirectory(projectFolder.toFile(), testProjectDir.getRoot());
        return GradleRunner.create()
                .withProjectDir(testProjectDir.getRoot())
                .withGradleVersion(gradleVersion)
                .withDebug(true)
                .forwardOutput();

    }

}
