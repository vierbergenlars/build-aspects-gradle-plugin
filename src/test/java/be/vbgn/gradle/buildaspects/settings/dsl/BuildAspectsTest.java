package be.vbgn.gradle.buildaspects.settings.dsl;

import be.vbgn.gradle.buildaspects.aspect.AspectHandler;
import be.vbgn.gradle.buildaspects.settings.project.ProjectHandler;
import be.vbgn.gradle.buildaspects.settings.project.VariantProjectDescriptorFactory;
import org.gradle.api.initialization.ProjectDescriptor;
import org.gradle.api.initialization.Settings;
import org.junit.Test;
import org.mockito.Mockito;

public class BuildAspectsTest {

    private Settings createSettingsMock() {
        Settings settings = Mockito.mock(Settings.class, Mockito.RETURNS_SMART_NULLS);

        Mockito.when(settings.project(Mockito.anyString())).then(q -> {
            ProjectDescriptor projectDescriptor = Mockito.mock(ProjectDescriptor.class, Mockito.RETURNS_SMART_NULLS);
            String path = q.getArgument(0, String.class);
            String[] components = path.split(":");
            Mockito.when(projectDescriptor.getName()).thenReturn(components[components.length - 1]);
            Mockito.when(projectDescriptor.getPath()).thenReturn(path);
            return projectDescriptor;
        });
        return settings;
    }

    @Test
    public void configureAspectsAndProjects() {
        Settings settings = createSettingsMock();

        BuildAspects buildAspects = new BuildAspects(new AspectHandler(), new ProjectHandler(settings),
                n -> new VariantProjectDescriptorFactory(settings, n));

        buildAspects.aspects(aspects -> {
            aspects.create("systemVersion", String.class, aspect -> {
                aspect.add("1.0").add("2.0");
            });
            aspects.create("communityEdition", Boolean.class, aspect -> {
                aspect.add(true);
                aspect.add(false);
            });
        });

        buildAspects.projects(projects -> {
            projects.include(":submoduleA", ":systemB:submoduleB");
        });

        Mockito.verify(settings).include(":submoduleA", ":systemB:submoduleB");
        Mockito.verify(settings).include(":submoduleA:submoduleA-systemVersion-1.0-communityEdition-true");
        Mockito.verify(settings).include(":submoduleA:submoduleA-systemVersion-1.0-communityEdition-false");
        Mockito.verify(settings).include(":submoduleA:submoduleA-systemVersion-2.0-communityEdition-true");
        Mockito.verify(settings).include(":submoduleA:submoduleA-systemVersion-2.0-communityEdition-false");
        Mockito.verify(settings).include(":systemB:submoduleB:submoduleB-systemVersion-1.0-communityEdition-true");
        Mockito.verify(settings).include(":systemB:submoduleB:submoduleB-systemVersion-2.0-communityEdition-false");
        Mockito.verify(settings).include(":systemB:submoduleB:submoduleB-systemVersion-1.0-communityEdition-true");
        Mockito.verify(settings).include(":systemB:submoduleB:submoduleB-systemVersion-2.0-communityEdition-false");
    }

    @Test(expected = IllegalStateException.class)
    public void configureProjectsBeforeAspects() {
        Settings settings = createSettingsMock();

        BuildAspects buildAspects = new BuildAspects(new AspectHandler(), new ProjectHandler(settings),
                n -> new VariantProjectDescriptorFactory(settings, n));

        buildAspects.projects(projects -> {
            projects.include(":submoduleA");
        });

        buildAspects.aspects(aspects -> {
            aspects.create("bla", String.class, aspect -> {
            });
        });
    }

    @Test
    public void configureWithNamer() {
        Settings settings = createSettingsMock();

        BuildAspects buildAspects = new BuildAspects(new AspectHandler(), new ProjectHandler(settings),
                n -> new VariantProjectDescriptorFactory(settings, n));

        buildAspects.projectNamer(desc -> desc.getParentProjectDescriptor().getName()
                + "-" + desc.getVariant().getProperty("systemVersion")
                + "-" + (((boolean) desc.getVariant().getProperty("communityEdition")) ? "community" : "enterprise"));

        buildAspects.aspects(aspects -> {
            aspects.create("systemVersion", String.class, aspect -> {
                aspect.add("1.0").add("2.0");
            });
            aspects.create("communityEdition", Boolean.class, aspect -> {
                aspect.add(true);
                aspect.add(false);
            });
        });

        buildAspects.projects(projects -> {
            projects.include(":submoduleA", ":systemB:submoduleB");
        });

        Mockito.verify(settings).include(":submoduleA", ":systemB:submoduleB");
        Mockito.verify(settings).include(":submoduleA:submoduleA-1.0-community");
        Mockito.verify(settings).include(":submoduleA:submoduleA-1.0-enterprise");
        Mockito.verify(settings).include(":submoduleA:submoduleA-2.0-community");
        Mockito.verify(settings).include(":submoduleA:submoduleA-2.0-enterprise");
        Mockito.verify(settings).include(":systemB:submoduleB:submoduleB-1.0-community");
        Mockito.verify(settings).include(":systemB:submoduleB:submoduleB-2.0-enterprise");
        Mockito.verify(settings).include(":systemB:submoduleB:submoduleB-1.0-community");
        Mockito.verify(settings).include(":systemB:submoduleB:submoduleB-2.0-enterprise");
    }

    @Test(expected = IllegalStateException.class)
    public void configureWithNamerAfterProjects() {

        Settings settings = createSettingsMock();

        BuildAspects buildAspects = new BuildAspects(new AspectHandler(), new ProjectHandler(settings),
                n -> new VariantProjectDescriptorFactory(settings, n));

        buildAspects.projects(projects -> {
            projects.include(":submoduleA");
        });

        buildAspects.projectNamer(desc -> desc.getParentProjectDescriptor().getName()
                + "-" + desc.getVariant().getProperty("systemVersion")
                + "-" + (((boolean) desc.getVariant().getProperty("communityEdition")) ? "community" : "enterprise"));
    }

}
