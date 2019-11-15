package be.vbgn.gradle.buildaspects.settings.dsl;

import be.vbgn.gradle.buildaspects.aspect.AspectHandler;
import be.vbgn.gradle.buildaspects.internal.OnetimeFactory;
import be.vbgn.gradle.buildaspects.settings.project.DefaultVariantProjectNamer;
import be.vbgn.gradle.buildaspects.settings.project.ParentVariantProjectDescriptor;
import be.vbgn.gradle.buildaspects.settings.project.ProjectHandler;
import be.vbgn.gradle.buildaspects.settings.project.VariantProjectDescriptor;
import be.vbgn.gradle.buildaspects.settings.project.VariantProjectDescriptorFactory;
import be.vbgn.gradle.buildaspects.variant.Variant;
import be.vbgn.gradle.buildaspects.variant.VariantBuilder;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import javax.inject.Inject;
import org.gradle.api.Namer;
import org.gradle.api.initialization.Settings;
import org.gradle.api.model.ObjectFactory;

public class BuildAspectsImpl implements BuildAspects {

    private final AspectHandler aspectHandler;
    private final ProjectHandler projectHandler;


    private final Set<VariantProjectDescriptor> variantProjectDescriptors = new HashSet<>();
    private final OnetimeFactory<Namer<ParentVariantProjectDescriptor>, VariantProjectDescriptorFactory> variantProjectBuilderOnetimeFactory;

    @Inject
    public BuildAspectsImpl(ObjectFactory objectFactory, Settings settings) {
        this(
                objectFactory.newInstance(AspectHandler.class),
                objectFactory.newInstance(ProjectHandler.class, settings),
                namer -> new VariantProjectDescriptorFactory(settings, namer)
        );
    }

    BuildAspectsImpl(AspectHandler aspectHandler, ProjectHandler projectHandler,
            Function<Namer<ParentVariantProjectDescriptor>, VariantProjectDescriptorFactory> variantProjectFactoryFactory) {
        this.aspectHandler = aspectHandler;
        this.projectHandler = projectHandler;
        variantProjectBuilderOnetimeFactory = new OnetimeFactory<>(variantProjectFactoryFactory);
        variantProjectBuilderOnetimeFactory.setSource(new DefaultVariantProjectNamer());
        VariantBuilder variantBuilder = new VariantBuilder();

        aspectHandler.aspectAdded(variantBuilder::addAspect);
        aspectHandler.aspectAdded(a -> {
            if (!projectHandler.getProjects().isEmpty()) {
                throw new IllegalStateException("You can not modify aspects after projects have been registered.");
            }
        });
        projectHandler.projectAdded(projectDescriptor -> {
            for (Variant variant : variantBuilder.getVariants()) {
                variantProjectDescriptors
                        .add(variantProjectBuilderOnetimeFactory.build().createProject(projectDescriptor, variant));
            }
        });
    }

    @Override
    public AspectHandler getAspects() {
        return aspectHandler;
    }

    @Override
    public ProjectHandler getProjects() {
        return projectHandler;
    }

    @Override
    public void setProjectNamer(Namer<ParentVariantProjectDescriptor> namer) {
        variantProjectBuilderOnetimeFactory.setSource(namer);
    }

    @Override
    public Set<VariantProjectDescriptor> getVariantProjects() {
        return Collections.unmodifiableSet(variantProjectDescriptors);
    }
}
