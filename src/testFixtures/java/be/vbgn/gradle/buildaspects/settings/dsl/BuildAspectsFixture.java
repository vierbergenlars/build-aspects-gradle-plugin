package be.vbgn.gradle.buildaspects.settings.dsl;

import be.vbgn.gradle.buildaspects.aspect.AspectHandlerFixture;
import be.vbgn.gradle.buildaspects.fixtures.ExtensionContainerFixture;
import be.vbgn.gradle.buildaspects.settings.project.ProjectHandler;
import be.vbgn.gradle.buildaspects.settings.project.VariantProjectDescriptorFactory;
import org.gradle.api.initialization.Settings;
import org.gradle.api.plugins.ExtensionContainer;

public class BuildAspectsFixture extends BuildAspectsImpl {

    private final ExtensionContainer extensionContainer = new ExtensionContainerFixture();

    public BuildAspectsFixture(Settings settings) {
        super(new AspectHandlerFixture(), new ProjectHandler(settings),
                namer -> new VariantProjectDescriptorFactory(settings, namer));
    }

    @Override
    public ExtensionContainer getExtensions() {
        return extensionContainer;
    }
}
