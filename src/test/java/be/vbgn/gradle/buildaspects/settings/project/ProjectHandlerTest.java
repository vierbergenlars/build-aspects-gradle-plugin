package be.vbgn.gradle.buildaspects.settings.project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicBoolean;
import org.gradle.api.initialization.ProjectDescriptor;
import org.gradle.api.initialization.Settings;
import org.junit.Test;
import org.mockito.Mockito;

public class ProjectHandlerTest {

    @Test
    public void project() {
        Settings settings = Mockito.mock(Settings.class, Mockito.RETURNS_SMART_NULLS);
        ProjectDescriptor projectA = Mockito.mock(ProjectDescriptor.class, Mockito.RETURNS_SMART_NULLS);
        Mockito.when(settings.project(":projectA")).thenReturn(projectA);
        Mockito.when(projectA.getPath()).thenReturn(":projectA");
        Mockito.when(projectA.getName()).thenReturn("projectA");

        ProjectHandler projectHandler = new ProjectHandler(settings);
        projectHandler.project(":projectA");

        assertEquals(Collections.singleton(projectA), new HashSet<>(projectHandler.getProjects()));
    }

    @Test
    public void include() {
        Settings settings = Mockito.mock(Settings.class, Mockito.RETURNS_SMART_NULLS);
        ProjectDescriptor projectA = Mockito.mock(ProjectDescriptor.class, Mockito.RETURNS_SMART_NULLS);
        Mockito.when(settings.project(":projectA")).thenReturn(projectA);
        Mockito.when(projectA.getPath()).thenReturn(":projectA");
        Mockito.when(projectA.getName()).thenReturn("projectA");

        ProjectHandler projectHandler = new ProjectHandler(settings);
        projectHandler.include(":projectA");

        assertEquals(Collections.singleton(projectA), new HashSet<>(projectHandler.getProjects()));
        Mockito.verify(settings).include(":projectA");
    }

    @Test
    public void addProjectFiresListener() {
        Settings settings = Mockito.mock(Settings.class, Mockito.RETURNS_SMART_NULLS);
        ProjectHandler handler = new ProjectHandler(settings);
        AtomicBoolean handlerFired = new AtomicBoolean(false);

        handler.projectAdded(a -> handlerFired.set(true));

        assertFalse(handlerFired.get());

        handler.project(":projectA");

        assertTrue(handlerFired.get());

    }

}
