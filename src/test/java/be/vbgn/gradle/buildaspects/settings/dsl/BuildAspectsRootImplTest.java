package be.vbgn.gradle.buildaspects.settings.dsl;

import be.vbgn.gradle.buildaspects.internal.PluginManager;
import be.vbgn.gradle.buildaspects.settings.project.DuplicateProjectException;
import org.gradle.api.initialization.Settings;
import org.gradle.api.plugins.ExtensionContainer;
import org.junit.Test;
import org.mockito.Mockito;

public class BuildAspectsRootImplTest extends BuildAspectsImplTest {

    @Override
    protected BuildAspects createBuildAspects(Settings settings) {
        return new BuildAspectsRootImpl(() -> super.createBuildAspects(settings), new PluginManager()) {
            @Override
            public ExtensionContainer getExtensions() {
                return null;
            }
        };
    }

    @Test
    public void createNested() {
        Settings settings = createSettingsMock();
        BuildAspectsRoot buildAspects = (BuildAspectsRoot) createBuildAspects(settings);

        buildAspects.nested(buildAspects1 -> {
            buildAspects1.getAspects().create("systemVersion", "1.0", "2.0");
            buildAspects1.getAspects().create("communityEdition", true, false);
            buildAspects1.getProjects().include(":moduleA");
        });

        buildAspects.nested(buildAspects1 -> {
            buildAspects1.getAspects().create("systemVersion", "2.1", "2.0");
            buildAspects1.getProjects().include(":moduleB");
        });

        Mockito.verify(settings).include(":moduleA");
        Mockito.verify(settings).include(":moduleA:moduleA-systemVersion-1.0-communityEdition-true");
        Mockito.verify(settings).include(":moduleA:moduleA-systemVersion-1.0-communityEdition-false");
        Mockito.verify(settings).include(":moduleA:moduleA-systemVersion-2.0-communityEdition-true");
        Mockito.verify(settings).include(":moduleA:moduleA-systemVersion-2.0-communityEdition-false");
        Mockito.verify(settings).include(":moduleB");
        Mockito.verify(settings).include(":moduleB:moduleB-systemVersion-2.1");
        Mockito.verify(settings).include(":moduleB:moduleB-systemVersion-2.0");
    }

    @Test(expected = DuplicateProjectException.class)
    public void createNestedDuplicateProject() {
        Settings settings = createSettingsMock();
        BuildAspectsRoot buildAspects = (BuildAspectsRoot) createBuildAspects(settings);

        buildAspects.nested(buildAspects1 -> {
            buildAspects1.getAspects().create("systemVersion", "1.0", "2.0");
            buildAspects1.getProjects().include(":moduleA");
        });

        buildAspects.nested(buildAspects1 -> {
            buildAspects1.getAspects().create("systemVersion", "2.1", "2.0");
            buildAspects1.getProjects().include(":moduleA");
        });

    }

    @Test(expected = IllegalBuildAspectsStateException.class)
    public void useNestedAndToplevelBuildAspects() {
        Settings settings = createSettingsMock();
        BuildAspectsRoot buildAspects = (BuildAspectsRoot) createBuildAspects(settings);

        buildAspects.nested(buildAspects1 -> {
        });
        buildAspects.aspects(aspectHandler -> {
        });
    }

    @Test(expected = IllegalBuildAspectsStateException.class)
    public void useToplevelAndNestedBuildAspects() {
        Settings settings = createSettingsMock();
        BuildAspectsRoot buildAspects = (BuildAspectsRoot) createBuildAspects(settings);

        buildAspects.aspects(aspectHandler -> {
        });
        buildAspects.nested(buildAspects1 -> {
        });
    }
}
