package be.vbgn.gradle.buildaspects.settings.project;

import static org.junit.Assert.assertEquals;

import be.vbgn.gradle.buildaspects.TestUtil;
import java.util.Collections;
import org.gradle.api.initialization.ProjectDescriptor;
import org.junit.Test;
import org.mockito.Mockito;

public class DefaultVariantProjectNamerTest {

    @Test
    public void determineName() {
        DefaultVariantProjectNamer namer = new DefaultVariantProjectNamer();
        ProjectDescriptor projectDescriptor = Mockito.mock(ProjectDescriptor.class, Mockito.RETURNS_SMART_NULLS);
        ParentVariantProjectDescriptor parentVariantProjectDescriptor = new ParentVariantProjectDescriptor(
                projectDescriptor,
                TestUtil.createVariant(Collections.singletonMap("aspect1", "value1")));
        Mockito.when(projectDescriptor.getName()).thenReturn("projectA");
        String generatedName = namer.determineName(parentVariantProjectDescriptor);

        assertEquals("projectA-aspect1-value1", generatedName);
    }

    @Test
    public void determineNameMultipleComponents() {
        DefaultVariantProjectNamer namer = new DefaultVariantProjectNamer();
        ProjectDescriptor projectDescriptor = Mockito.mock(ProjectDescriptor.class, Mockito.RETURNS_SMART_NULLS);
        ParentVariantProjectDescriptor parentVariantProjectDescriptor = new ParentVariantProjectDescriptor(
                projectDescriptor,
                TestUtil.createVariant(Collections.singletonMap("aspect1", "value1"),
                        Collections.singletonMap("aspect2", "value2")));
        Mockito.when(projectDescriptor.getName()).thenReturn("projectA");
        String generatedName = namer.determineName(parentVariantProjectDescriptor);

        assertEquals("projectA-aspect1-value1-aspect2-value2", generatedName);
    }

}
