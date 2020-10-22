package be.vbgn.gradle.buildaspects.aspect;

import be.vbgn.gradle.buildaspects.internal.fixtures.ExtensionContainerFixture;
import org.gradle.api.plugins.ExtensionContainer;

public class AspectHandlerFixture extends AspectHandler {

    private final ExtensionContainer extensionContainer = new ExtensionContainerFixture();

    @Override
    public ExtensionContainer getExtensions() {
        return extensionContainer;
    }
}
