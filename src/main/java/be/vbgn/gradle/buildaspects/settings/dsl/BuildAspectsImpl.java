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
import java.util.function.Predicate;
import javax.inject.Inject;
import org.gradle.api.Namer;
import org.gradle.api.initialization.Settings;
import org.gradle.api.model.ObjectFactory;

public class BuildAspectsImpl implements BuildAspects {

    private final AspectHandler aspectHandler;
    private final ProjectHandler projectHandler;

    private Predicate<ParentVariantProjectDescriptor> excluder;
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
        excluder = p -> false;
        variantProjectBuilderOnetimeFactory = new OnetimeFactory<>(variantProjectFactoryFactory,
                IllegalBuildAspectsStateException.modifyNamerAfterProjects());
        variantProjectBuilderOnetimeFactory.setSource(new DefaultVariantProjectNamer());
        VariantBuilder variantBuilder = new VariantBuilder();

        aspectHandler.aspectAdded(variantBuilder::addAspect);
        aspectHandler.aspectAdded(a -> {
            if (!projectHandler.getProjects().isEmpty()) {
                throw IllegalBuildAspectsStateException.modifyAspectsAfterProjects();
            }
        });
        aspectHandler.calculatedPropertyAdded(variantBuilder::addCalculatedPropertyBuilder);
        projectHandler.projectAdded(projectDescriptor -> {
            for (Variant variant : variantBuilder.getVariants()) {
                VariantProjectDescriptorFactory factory = variantProjectBuilderOnetimeFactory.build();
                if (!excluder.test(factory.createParentProjectDescriptor(projectDescriptor, variant))) {
                    variantProjectDescriptors
                            .add(variantProjectBuilderOnetimeFactory.build().createProject(projectDescriptor, variant));
                }
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
    public synchronized void exclude(Predicate<ParentVariantProjectDescriptor> excluder) {
        if (!getProjects().getProjects().isEmpty()) {
            throw IllegalBuildAspectsStateException.modifyExcludeAfterProjects();
        }
        this.excluder = excluder.or(this.excluder);
    }

    @Override
    public Set<VariantProjectDescriptor> getVariantProjects() {
        return Collections.unmodifiableSet(variantProjectDescriptors);
    }
}
