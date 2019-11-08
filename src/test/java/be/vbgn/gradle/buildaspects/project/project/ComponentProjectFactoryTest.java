package be.vbgn.gradle.buildaspects.project.project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import be.vbgn.gradle.buildaspects.TestUtil;
import be.vbgn.gradle.buildaspects.component.Component;
import be.vbgn.gradle.buildaspects.settings.project.ComponentProjectDescriptor;
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

public class ComponentProjectFactoryTest {

    private ProjectDescriptor createProjectDescriptor(String path) {
        ProjectDescriptor projectDescriptor = Mockito.mock(ProjectDescriptor.class, Mockito.RETURNS_SMART_NULLS);
        Mockito.when(projectDescriptor.getPath()).thenReturn(path);
        return projectDescriptor;
    }

    private ComponentProjectDescriptor createComponentProjectDescriptor(String path, Component component) {
        ComponentProjectDescriptor componentProjectDescriptor = Mockito
                .mock(ComponentProjectDescriptor.class, Mockito.RETURNS_SMART_NULLS);
        ProjectDescriptor projectDescriptor = createProjectDescriptor(path);
        Mockito.when(componentProjectDescriptor.getProjectDescriptor()).thenReturn(projectDescriptor);
        ProjectDescriptor parentProjectDescriptor = createProjectDescriptor(path.substring(0, path.lastIndexOf(':')));
        Mockito.when(componentProjectDescriptor.getParentProjectDescriptor())
                .thenReturn(parentProjectDescriptor);
        Mockito.when(componentProjectDescriptor.getComponent()).thenReturn(component);
        return componentProjectDescriptor;
    }

    @Test
    public void createComponentProject() {
        Set<ComponentProjectDescriptor> componentProjectDescriptors = new HashSet<>();
        componentProjectDescriptors.add(createComponentProjectDescriptor(":projectA:projectA-c",
                TestUtil.createComponent(Collections.singletonMap("systemVersion", "1.0"))));
        componentProjectDescriptors.add(createComponentProjectDescriptor(":projectA:projectA-d",
                TestUtil.createComponent(Collections.singletonMap("systemVersion", "2.0"))));

        ComponentProjectFactory componentProjectFactory = new ComponentProjectFactory(componentProjectDescriptors);

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

        Optional<ComponentProject> componentProject = componentProjectFactory.createComponentProject(projectAC);

        assertTrue(componentProject.isPresent());
        assertEquals(projectAC, componentProject.get().getProject());
        assertEquals(TestUtil.createComponent(Collections.singletonMap("systemVersion", "1.0")),
                componentProject.get().getComponent());

        Optional<ComponentProject> componentProject1 = componentProjectFactory.createComponentProject(projectA);
        assertFalse(componentProject1.isPresent());
    }

    @Test
    public void createComponentProjectsForParent() {
        Set<ComponentProjectDescriptor> componentProjectDescriptors = new HashSet<>();
        componentProjectDescriptors.add(createComponentProjectDescriptor(":projectA:projectA-c",
                TestUtil.createComponent(Collections.singletonMap("systemVersion", "1.0"))));
        componentProjectDescriptors.add(createComponentProjectDescriptor(":projectA:projectA-d",
                TestUtil.createComponent(Collections.singletonMap("systemVersion", "2.0"))));
        componentProjectDescriptors.add(createComponentProjectDescriptor(":projectB:projectB-c",
                TestUtil.createComponent(Collections.singletonMap("systemVersion", "1.0"))));

        ComponentProjectFactory componentProjectFactory = new ComponentProjectFactory(componentProjectDescriptors);

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

        Set<ComponentProject> componentProjects = componentProjectFactory.createComponentProjectsForParent(projectA);

        assertEquals(new HashSet<>(Arrays.asList(
                new ComponentProject(projectAC,
                        TestUtil.createComponent(Collections.singletonMap("systemVersion", "1.0"))),
                new ComponentProject(projectAD,
                        TestUtil.createComponent(Collections.singletonMap("systemVersion", "2.0")))
        )), componentProjects);

        Set<ComponentProject> componentProjects1 = componentProjectFactory.createComponentProjectsForParent(projectAC);
        assertTrue(componentProjects1.isEmpty());
        Set<ComponentProject> componentProjects2 = componentProjectFactory
                .createComponentProjectsForParent(rootProject);
        assertTrue(componentProjects2.isEmpty());
    }
}
