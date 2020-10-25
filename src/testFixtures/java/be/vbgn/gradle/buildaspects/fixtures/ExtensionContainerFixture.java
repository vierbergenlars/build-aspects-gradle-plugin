package be.vbgn.gradle.buildaspects.fixtures;

import org.gradle.internal.extensibility.DefaultConvention;

public class ExtensionContainerFixture extends DefaultConvention {

    public ExtensionContainerFixture() {
        super(InstantiatorFixture.INSTANCE);
    }
}
