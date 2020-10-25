package be.vbgn.gradle.buildaspects.settings.dsl;

import be.vbgn.gradle.buildaspects.fixtures.ExtensionContainerFixture;
import be.vbgn.gradle.buildaspects.internal.PluginManager;
import org.gradle.api.initialization.Settings;
import org.gradle.api.plugins.ExtensionContainer;

public class BuildAspectsRootFixture extends BuildAspectsRootImpl {

    private final ExtensionContainer extensionContainer = new ExtensionContainerFixture();

    public BuildAspectsRootFixture(Settings settings, PluginManager<BuildAspects> pluginManager) {
        super(() -> new BuildAspectsFixture(settings), pluginManager);
    }

    @Override
    public ExtensionContainer getExtensions() {
        return extensionContainer;
    }
}
