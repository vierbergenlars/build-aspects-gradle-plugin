package be.vbgn.gradle.buildaspects.settings.dsl;

import be.vbgn.gradle.buildaspects.TestUtil;
import be.vbgn.gradle.buildaspects.settings.project.ProjectHandler;
import be.vbgn.gradle.buildaspects.settings.project.VariantProjectDescriptorFactory;
import org.gradle.api.initialization.Settings;
import org.gradle.api.plugins.ExtensionContainer;

public class BuildAspectsImplTest extends AbstractBuildAspectsTest {

    @Override
    protected BuildAspects createBuildAspects(Settings settings) {
        return new BuildAspectsImpl(TestUtil.createAspectHandler(), new ProjectHandler(settings),
                n -> new VariantProjectDescriptorFactory(settings, n)) {
            @Override
            public ExtensionContainer getExtensions() {
                return null;
            }
        };
    }
}
