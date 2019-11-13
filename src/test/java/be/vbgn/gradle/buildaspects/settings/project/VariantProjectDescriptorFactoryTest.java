package be.vbgn.gradle.buildaspects.settings.project;

import static org.junit.Assert.assertEquals;

import be.vbgn.gradle.buildaspects.TestUtil;
import be.vbgn.gradle.buildaspects.variant.Variant;
import java.util.Collections;
import org.gradle.api.initialization.ProjectDescriptor;
import org.gradle.api.initialization.Settings;
import org.junit.Test;
import org.mockito.Mockito;

public class VariantProjectDescriptorFactoryTest {

    @Test
    public void createProject() {
        Settings settings = Mockito.mock(Settings.class, Mockito.RETURNS_SMART_NULLS);
        VariantProjectDescriptorFactory factory = new VariantProjectDescriptorFactory(settings,
                p -> "createdProject");
        ProjectDescriptor createdProject = Mockito.mock(ProjectDescriptor.class, Mockito.RETURNS_SMART_NULLS);
        Mockito.when(settings.project(":projectA:createdProject")).thenReturn(createdProject);
        ProjectDescriptor projectA = Mockito.mock(ProjectDescriptor.class, Mockito.RETURNS_SMART_NULLS);
        Mockito.when(projectA.getName()).thenReturn("projectA");
        Mockito.when(projectA.getPath()).thenReturn(":projectA");

        Variant variant = TestUtil.createVariant(Collections.singletonMap("aspect1", "value1"));

        VariantProjectDescriptor variantProjectDescriptor = factory.createProject(projectA, variant);

        Mockito.verify(settings).include(":projectA:createdProject");

        assertEquals(createdProject, variantProjectDescriptor.getProjectDescriptor());
        assertEquals(projectA, variantProjectDescriptor.getParentProjectDescriptor());
        assertEquals(variant, variantProjectDescriptor.getVariant());
    }

    @Test
    public void createProjectFromRootProject() {
        Settings settings = Mockito.mock(Settings.class, Mockito.RETURNS_SMART_NULLS);
        VariantProjectDescriptorFactory factory = new VariantProjectDescriptorFactory(settings,
                p -> "createdProject");
        ProjectDescriptor createdProject = Mockito.mock(ProjectDescriptor.class, Mockito.RETURNS_SMART_NULLS);
        Mockito.when(settings.project(":createdProject")).thenReturn(createdProject);
        ProjectDescriptor projectA = Mockito.mock(ProjectDescriptor.class, Mockito.RETURNS_SMART_NULLS);
        Mockito.when(projectA.getName()).thenReturn("projectRoot");
        Mockito.when(projectA.getPath()).thenReturn(":");

        Variant variant = TestUtil.createVariant(Collections.singletonMap("aspect1", "value1"));

        VariantProjectDescriptor variantProjectDescriptor = factory.createProject(projectA, variant);

        Mockito.verify(settings).include(":createdProject");

        assertEquals(createdProject, variantProjectDescriptor.getProjectDescriptor());
        assertEquals(projectA, variantProjectDescriptor.getParentProjectDescriptor());
        assertEquals(variant, variantProjectDescriptor.getVariant());
    }
}
