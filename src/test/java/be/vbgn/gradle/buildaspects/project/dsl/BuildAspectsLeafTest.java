package be.vbgn.gradle.buildaspects.project.dsl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import be.vbgn.gradle.buildaspects.TestUtil;
import be.vbgn.gradle.buildaspects.variant.Variant;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Test;

public class BuildAspectsLeafTest {

    @Test
    public void when() {
        Variant variant = TestUtil.createVariant(Collections.singletonMap("systemVersion", "1.0"));
        Project project = ProjectBuilder.builder().build();

        BuildAspectsLeaf buildAspectsLeaf = new BuildAspectsLeaf(project, variant);

        AtomicBoolean whenCalled = new AtomicBoolean(false);

        buildAspectsLeaf.when(c -> c.getProperty("systemVersion").equals("1.0"), p -> {
            whenCalled.set(true);
        });

        assertTrue(whenCalled.get());
    }

    @Test
    public void whenNotCalled() {
        Variant variant = TestUtil.createVariant(Collections.singletonMap("systemVersion", "1.0"));
        Project project = ProjectBuilder.builder().build();

        BuildAspectsLeaf buildAspectsLeaf = new BuildAspectsLeaf(project, variant);

        AtomicBoolean whenCalled = new AtomicBoolean(false);

        buildAspectsLeaf.when(c -> c.getProperty("systemVersion").equals("2.0"), p -> {
            whenCalled.set(true);
        });

        assertFalse(whenCalled.get());
    }
}
