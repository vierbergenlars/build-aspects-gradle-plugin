package be.vbgn.gradle.buildaspects.plugins;

import static org.junit.Assert.assertEquals;

import be.vbgn.gradle.buildaspects.BuildAspectsPlugin;
import be.vbgn.gradle.buildaspects.fixtures.InstantiatorFixture;
import be.vbgn.gradle.buildaspects.fixtures.SettingsFixture;
import be.vbgn.gradle.buildaspects.settings.dsl.BuildAspectsRoot;
import be.vbgn.gradle.buildaspects.settings.dsl.IllegalBuildAspectsStateException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.gradle.api.initialization.ProjectDescriptor;
import org.gradle.api.initialization.Settings;
import org.junit.Test;

public class CustomAspectObjectPluginTest {

    @Test
    public void testApplyCustomObjectPluginGettingAspects() {
        InstantiatorFixture.allowInstantiationFor(AlfrescoVersionAction.class);

        Settings settings = new SettingsFixture();
        BuildAspectsPlugin buildAspectsPlugin = new BuildAspectsPlugin();
        buildAspectsPlugin.apply(settings);

        buildAspectsPlugin.applyPlugin(new AlfrescoVersionPlugin());

        BuildAspectsRoot buildAspectsRoot = settings.getExtensions().getByType(BuildAspectsRoot.class);

        AlfrescoVersionAction versionAction = buildAspectsRoot.getExtensions().getByType(AlfrescoVersionAction.class);

        versionAction.version("1.2.2").enterprise();
        versionAction.version("1.2.a").community();

        assertEquals(1, buildAspectsRoot.getAspects().getAspects().size());
    }

    @Test
    public void testApplyCustomObjectPluginConfiguringProjects() {
        InstantiatorFixture.allowInstantiationFor(AlfrescoVersionAction.class);

        Settings settings = new SettingsFixture();
        BuildAspectsPlugin buildAspectsPlugin = new BuildAspectsPlugin();
        buildAspectsPlugin.apply(settings);

        buildAspectsPlugin.applyPlugin(new AlfrescoVersionPlugin());

        BuildAspectsRoot buildAspectsRoot = settings.getExtensions().getByType(BuildAspectsRoot.class);

        AlfrescoVersionAction versionAction = buildAspectsRoot.getExtensions().getByType(AlfrescoVersionAction.class);

        versionAction.version("1.2.2").enterprise();
        versionAction.version("1.2.a").community();

        buildAspectsRoot.getProjects().include(":project");

        Set<String> projects = settings.project(":project").getChildren().stream().map(ProjectDescriptor::getPath)
                .collect(Collectors.toSet());

        assertEquals(new HashSet<>(Arrays.asList(":project:project-alfresco-1.2.2-enterprise",
                ":project:project-alfresco-1.2.a-community")), projects);
    }

    @Test(expected = IllegalBuildAspectsStateException.class)
    public void addCustomObjectAfterFinalisation() {
        InstantiatorFixture.allowInstantiationFor(AlfrescoVersionAction.class);

        Settings settings = new SettingsFixture();
        BuildAspectsPlugin buildAspectsPlugin = new BuildAspectsPlugin();
        buildAspectsPlugin.apply(settings);

        buildAspectsPlugin.applyPlugin(new AlfrescoVersionPlugin());

        BuildAspectsRoot buildAspectsRoot = settings.getExtensions().getByType(BuildAspectsRoot.class);

        AlfrescoVersionAction versionAction = buildAspectsRoot.getExtensions().getByType(AlfrescoVersionAction.class);
        versionAction.version("1.2.2").enterprise();

        buildAspectsRoot.getProjects().include(":project");

        versionAction.version("1.2.a").community();
    }

    public static class AlfrescoVersionPlugin extends CustomAspectObjectPlugin<AlfrescoVersion, AlfrescoVersionAction> {

        public AlfrescoVersionPlugin() {
            super(CustomAspectObjectPlugin
                    .configuration(AlfrescoVersion.class, AlfrescoVersionAction.class)
                    .aspectName("alfresco")
                    .createCalculatedAspects((aspectHandler, getVersion) -> {
                        aspectHandler.calculated("alfrescoVersion", getVersion.andThen(version -> version.version));
                        aspectHandler.calculated("alfrescoCommunity", getVersion.andThen(version -> version.community));
                    })
                    .build());
        }
    }

    public static class AlfrescoVersionAction {

        private final Consumer<AlfrescoVersion> addVersionDispatcher;

        public AlfrescoVersionAction(
                Consumer<AlfrescoVersion> addVersionDispatcher) {
            this.addVersionDispatcher = addVersionDispatcher;
        }


        public AlfrescoVersion version(String v) {
            AlfrescoVersion version = new AlfrescoVersion(v);
            addVersionDispatcher.accept(version);
            return version;
        }
    }

    public static class AlfrescoVersion {

        private String version;
        private boolean community = false;

        public AlfrescoVersion(String version) {
            this.version = version;
        }

        public void community(boolean community) {
            this.community = community;
        }

        public void community() {
            community(true);
        }

        public void enterprise() {
            community(false);
        }

        @Override
        public String toString() {
            return version + (community ? "-community" : "-enterprise");
        }
    }

}
