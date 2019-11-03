package be.vbgn.gradle.buildaspects.project;

import static org.junit.Assert.assertEquals;

import be.vbgn.gradle.buildaspects.TestUtil;
import be.vbgn.gradle.buildaspects.component.Component;
import java.util.Collections;
import org.gradle.api.initialization.ProjectDescriptor;
import org.gradle.api.initialization.Settings;
import org.junit.Test;
import org.mockito.Mockito;

public class ComponentProjectFactoryTest {

    @Test
    public void createProject() {
        Settings settings = Mockito.mock(Settings.class, Mockito.RETURNS_SMART_NULLS);
        ComponentProjectFactory factory = new ComponentProjectFactory(settings, p -> "createdProject");
        ProjectDescriptor createdProject = Mockito.mock(ProjectDescriptor.class, Mockito.RETURNS_SMART_NULLS);
        Mockito.when(settings.project(":projectA:createdProject")).thenReturn(createdProject);
        ProjectDescriptor projectA = Mockito.mock(ProjectDescriptor.class, Mockito.RETURNS_SMART_NULLS);
        Mockito.when(projectA.getName()).thenReturn("projectA");
        Mockito.when(projectA.getPath()).thenReturn(":projectA");

        Component component = TestUtil.createComponent(Collections.singletonMap("aspect1", "value1"));

        ComponentProject componentProject = factory.createProject(projectA, component);

        Mockito.verify(settings).include(":projectA:createdProject");

        assertEquals(createdProject, componentProject.getProjectDescriptor());
        assertEquals(projectA, componentProject.getParentProjectDescriptor());
        assertEquals(component, componentProject.getComponent());
    }

}
