package be.vbgn.gradle.buildaspects.project.dsl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import be.vbgn.gradle.buildaspects.TestUtil;
import be.vbgn.gradle.buildaspects.project.project.VariantProject;
import be.vbgn.gradle.buildaspects.variant.Variant;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Test;
import org.mockito.Mockito;

public class BuildAspectsParentTest {

    private VariantProject createVariantProject(Project project, Variant variant) {
        VariantProject variantProject = Mockito.mock(VariantProject.class, Mockito.RETURNS_SMART_NULLS);
        Mockito.when(variantProject.getProject()).thenReturn(project);
        Mockito.when(variantProject.getVariant()).thenReturn(variant);
        return variantProject;
    }

    @Test
    public void when() {
        Variant variant = TestUtil.createVariant(Collections.singletonMap("systemVersion", "1.0"));
        Project rootProject = ProjectBuilder.builder().build();
        Project projectA = ProjectBuilder.builder().withParent(rootProject).withName("projectA").build();
        Project projectAC = ProjectBuilder.builder().withParent(projectA).withName("projectA-c").build();

        VariantProject variantProject = createVariantProject(projectAC, variant);

        BuildAspectsParent buildAspects = new BuildAspectsParent(projectA, Collections.singleton(variantProject));

        AtomicBoolean whenCalled = new AtomicBoolean(false);

        buildAspects.when(c -> c.getProperty("systemVersion").equals("1.0"), p -> {
            whenCalled.set(true);
        });

        assertTrue(whenCalled.get());
    }

    @Test
    public void whenNotCalled() {
        Variant variant = TestUtil.createVariant(Collections.singletonMap("systemVersion", "1.0"));
        Project rootProject = ProjectBuilder.builder().build();
        Project projectA = ProjectBuilder.builder().withParent(rootProject).withName("projectA").build();
        Project projectAC = ProjectBuilder.builder().withParent(projectA).withName("projectA-c").build();

        VariantProject variantProject = createVariantProject(projectAC, variant);

        BuildAspectsParent buildAspects = new BuildAspectsParent(projectA, Collections.singleton(variantProject));

        AtomicBoolean whenCalled = new AtomicBoolean(false);

        buildAspects.when(c -> c.getProperty("systemVersion").equals("2.0"), p -> {
            whenCalled.set(true);
        });

        assertFalse(whenCalled.get());
    }

}
