package be.vbgn.gradle.buildaspects.settings.dsl;

import be.vbgn.gradle.buildaspects.aspect.AspectHandler;
import be.vbgn.gradle.buildaspects.settings.project.ProjectHandler;
import be.vbgn.gradle.buildaspects.settings.project.VariantProjectDescriptorFactory;
import org.gradle.api.initialization.Settings;

public class BuildAspectsImplTest extends AbstractBuildAspectsTest {

    @Override
    protected BuildAspects createBuildAspects(Settings settings) {
        return new BuildAspectsImpl(new AspectHandler(), new ProjectHandler(settings),
                n -> new VariantProjectDescriptorFactory(settings, n));
    }
}
