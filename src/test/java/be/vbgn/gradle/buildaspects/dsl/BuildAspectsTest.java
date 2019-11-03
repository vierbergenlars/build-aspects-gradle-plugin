package be.vbgn.gradle.buildaspects.dsl;

import be.vbgn.gradle.buildaspects.aspect.AspectHandler;
import be.vbgn.gradle.buildaspects.project.ComponentProjectFactory;
import be.vbgn.gradle.buildaspects.project.ProjectHandler;
import org.gradle.api.initialization.ProjectDescriptor;
import org.gradle.api.initialization.Settings;
import org.junit.Test;
import org.mockito.Mockito;

public class BuildAspectsTest {

    @Test
    public void configureAspectsAndProjects() {
        Settings settings = Mockito.mock(Settings.class, Mockito.RETURNS_SMART_NULLS);

        Mockito.when(settings.project(Mockito.anyString())).then(q -> {
            ProjectDescriptor projectDescriptor = Mockito.mock(ProjectDescriptor.class, Mockito.RETURNS_SMART_NULLS);
            String path = q.getArgument(0, String.class);
            String[] components = path.split(":");
            Mockito.when(projectDescriptor.getName()).thenReturn(components[components.length-1]);
            Mockito.when(projectDescriptor.getPath()).thenReturn(path);
            return projectDescriptor;
        });

        BuildAspects buildAspects = new BuildAspects(new AspectHandler(), new ProjectHandler(settings),
                n -> new ComponentProjectFactory(settings, n));

        buildAspects.aspects(aspects -> {
            aspects.create("systemVersion", String.class)
                    .add("1.0")
                    .add("2.0");
            aspects.create("communityEdition", Boolean.class)
                    .add(true)
                    .add(false);
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

}
