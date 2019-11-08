package be.vbgn.gradle.buildaspects.project.dsl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import be.vbgn.gradle.buildaspects.TestUtil;
import be.vbgn.gradle.buildaspects.component.Component;
import be.vbgn.gradle.buildaspects.project.project.ComponentProject;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Test;
import org.mockito.Mockito;

public class BuildAspectsParentTest {

    private ComponentProject createComponentProject(Project project, Component component) {
        ComponentProject componentProject = Mockito.mock(ComponentProject.class, Mockito.RETURNS_SMART_NULLS);
        Mockito.when(componentProject.getProject()).thenReturn(project);
        Mockito.when(componentProject.getComponent()).thenReturn(component);
        return componentProject;
    }

    @Test
    public void when() {
        Component component = TestUtil.createComponent(Collections.singletonMap("systemVersion", "1.0"));
        Project rootProject = ProjectBuilder.builder().build();
        Project projectA = ProjectBuilder.builder().withParent(rootProject).withName("projectA").build();
        Project projectAC = ProjectBuilder.builder().withParent(projectA).withName("projectA-c").build();

        ComponentProject componentProject = createComponentProject(projectAC, component);

        BuildAspectsParent buildAspects = new BuildAspectsParent(projectA, Collections.singleton(componentProject));

        AtomicBoolean whenCalled = new AtomicBoolean(false);

        buildAspects.when(c -> c.getProperty("systemVersion").equals("1.0"), p -> {
            whenCalled.set(true);
        });

        assertTrue(whenCalled.get());
    }

    @Test
    public void whenNotCalled() {
        Component component = TestUtil.createComponent(Collections.singletonMap("systemVersion", "1.0"));
        Project rootProject = ProjectBuilder.builder().build();
        Project projectA = ProjectBuilder.builder().withParent(rootProject).withName("projectA").build();
        Project projectAC = ProjectBuilder.builder().withParent(projectA).withName("projectA-c").build();

        ComponentProject componentProject = createComponentProject(projectAC, component);

        BuildAspectsParent buildAspects = new BuildAspectsParent(projectA, Collections.singleton(componentProject));

        AtomicBoolean whenCalled = new AtomicBoolean(false);

        buildAspects.when(c -> c.getProperty("systemVersion").equals("2.0"), p -> {
            whenCalled.set(true);
        });

        assertFalse(whenCalled.get());
    }

}
