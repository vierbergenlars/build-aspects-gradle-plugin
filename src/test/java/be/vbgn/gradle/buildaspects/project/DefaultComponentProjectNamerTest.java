package be.vbgn.gradle.buildaspects.project;

import static org.junit.Assert.assertEquals;

import be.vbgn.gradle.buildaspects.TestUtil;
import java.util.Collections;
import org.gradle.api.initialization.ProjectDescriptor;
import org.junit.Test;
import org.mockito.Mockito;

public class DefaultComponentProjectNamerTest {

    @Test
    public void determineName() {
        DefaultComponentProjectNamer namer = new DefaultComponentProjectNamer();
        ProjectDescriptor projectDescriptor = Mockito.mock(ProjectDescriptor.class, Mockito.RETURNS_SMART_NULLS);
        ComponentProjectDescriptor componentProjectDescriptor = new ComponentProjectDescriptor(projectDescriptor,
                TestUtil.createComponent(Collections.singletonMap("aspect1", "value1")));
        Mockito.when(projectDescriptor.getName()).thenReturn("projectA");
        String generatedName = namer.determineName(componentProjectDescriptor);

        assertEquals("projectA-aspect1-value1", generatedName);
    }

    @Test
    public void determineNameMultipleComponents() {
        DefaultComponentProjectNamer namer = new DefaultComponentProjectNamer();
        ProjectDescriptor projectDescriptor = Mockito.mock(ProjectDescriptor.class, Mockito.RETURNS_SMART_NULLS);
        ComponentProjectDescriptor componentProjectDescriptor = new ComponentProjectDescriptor(projectDescriptor,
                TestUtil.createComponent(Collections.singletonMap("aspect1", "value1"),
                        Collections.singletonMap("aspect2", "value2")));
        Mockito.when(projectDescriptor.getName()).thenReturn("projectA");
        String generatedName = namer.determineName(componentProjectDescriptor);

        assertEquals("projectA-aspect1-value1-aspect2-value2", generatedName);
    }

}
