package be.vbgn.gradle.buildaspects.project.project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import be.vbgn.gradle.buildaspects.TestUtil;
import be.vbgn.gradle.buildaspects.settings.project.VariantProjectDescriptor;
import be.vbgn.gradle.buildaspects.variant.Variant;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.gradle.api.Project;
import org.gradle.api.initialization.ProjectDescriptor;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Test;
import org.mockito.Mockito;

public class VariantProjectFactoryTest {

    private ProjectDescriptor createProjectDescriptor(String path) {
        ProjectDescriptor projectDescriptor = Mockito.mock(ProjectDescriptor.class, Mockito.RETURNS_SMART_NULLS);
        Mockito.when(projectDescriptor.getPath()).thenReturn(path);
        return projectDescriptor;
    }

    private VariantProjectDescriptor createVariantProjectDescriptor(String path, Variant variant) {
        VariantProjectDescriptor variantProjectDescriptor = Mockito
                .mock(VariantProjectDescriptor.class, Mockito.RETURNS_SMART_NULLS);
        ProjectDescriptor projectDescriptor = createProjectDescriptor(path);
        Mockito.when(variantProjectDescriptor.getProjectDescriptor()).thenReturn(projectDescriptor);
        ProjectDescriptor parentProjectDescriptor = createProjectDescriptor(path.substring(0, path.lastIndexOf(':')));
        Mockito.when(variantProjectDescriptor.getParentProjectDescriptor())
                .thenReturn(parentProjectDescriptor);
        Mockito.when(variantProjectDescriptor.getVariant()).thenReturn(variant);
        return variantProjectDescriptor;
    }

    @Test
    public void createVariantProject() {
        Set<VariantProjectDescriptor> variantProjectDescriptors = new HashSet<>();
        variantProjectDescriptors.add(createVariantProjectDescriptor(":projectA:projectA-c",
                TestUtil.createVariant(Collections.singletonMap("systemVersion", "1.0"))));
        variantProjectDescriptors.add(createVariantProjectDescriptor(":projectA:projectA-d",
                TestUtil.createVariant(Collections.singletonMap("systemVersion", "2.0"))));

        VariantProjectFactory variantProjectFactory = new VariantProjectFactory(variantProjectDescriptors);

        Project rootProject = ProjectBuilder.builder()
                .withName(":")
                .build();
        Project projectA = ProjectBuilder.builder()
                .withName("projectA")
                .withParent(rootProject)
                .build();
        Project projectAC = ProjectBuilder.builder()
                .withName("projectA-c")
                .withParent(projectA)
                .build();

        Optional<VariantProject> variantProject = variantProjectFactory.createVariantProject(projectAC);

        assertTrue(variantProject.isPresent());
        assertEquals(projectAC, variantProject.get().getProject());
        assertEquals(TestUtil.createVariant(Collections.singletonMap("systemVersion", "1.0")),
                variantProject.get().getVariant());

        Optional<VariantProject> variantProject1 = variantProjectFactory.createVariantProject(projectA);
        assertFalse(variantProject1.isPresent());
    }

    @Test
    public void createVariantProjectsForParent() {
        Set<VariantProjectDescriptor> variantProjectDescriptors = new HashSet<>();
        variantProjectDescriptors.add(createVariantProjectDescriptor(":projectA:projectA-c",
                TestUtil.createVariant(Collections.singletonMap("systemVersion", "1.0"))));
        variantProjectDescriptors.add(createVariantProjectDescriptor(":projectA:projectA-d",
                TestUtil.createVariant(Collections.singletonMap("systemVersion", "2.0"))));
        variantProjectDescriptors.add(createVariantProjectDescriptor(":projectB:projectB-c",
                TestUtil.createVariant(Collections.singletonMap("systemVersion", "1.0"))));

        VariantProjectFactory variantProjectFactory = new VariantProjectFactory(variantProjectDescriptors);

        Project rootProject = ProjectBuilder.builder()
                .withName(":")
                .build();
        Project projectA = ProjectBuilder.builder()
                .withName("projectA")
                .withParent(rootProject)
                .build();
        Project projectAC = ProjectBuilder.builder()
                .withName("projectA-c")
                .withParent(projectA)
                .build();
        Project projectAD = ProjectBuilder.builder()
                .withName("projectA-d")
                .withParent(projectA)
                .build();

        Set<VariantProject> variantProjects = variantProjectFactory.createVariantProjectsForParent(projectA);

        assertEquals(new HashSet<>(Arrays.asList(
                new VariantProject(projectAC,
                        TestUtil.createVariant(Collections.singletonMap("systemVersion", "1.0"))),
                new VariantProject(projectAD,
                        TestUtil.createVariant(Collections.singletonMap("systemVersion", "2.0")))
        )), variantProjects);

        Set<VariantProject> variantProjects1 = variantProjectFactory.createVariantProjectsForParent(projectAC);
        assertTrue(variantProjects1.isEmpty());
        Set<VariantProject> variantProjects2 = variantProjectFactory
                .createVariantProjectsForParent(rootProject);
        assertTrue(variantProjects2.isEmpty());
    }
}
