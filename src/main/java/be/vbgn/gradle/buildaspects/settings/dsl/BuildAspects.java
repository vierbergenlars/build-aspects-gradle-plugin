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
import groovy.lang.Closure;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import javax.inject.Inject;
import org.gradle.api.Action;
import org.gradle.api.Namer;
import org.gradle.api.initialization.Settings;
import org.gradle.api.model.ObjectFactory;

public class BuildAspects {

    private final AspectHandler aspectHandler;
    private final ProjectHandler projectHandler;


    private final Set<VariantProjectDescriptor> variantProjectDescriptors = new HashSet<>();
    private final OnetimeFactory<Namer<ParentVariantProjectDescriptor>, VariantProjectDescriptorFactory> variantProjectBuilderOnetimeFactory;

    @Inject
    public BuildAspects(ObjectFactory objectFactory, Settings settings) {
        this(
                objectFactory.newInstance(AspectHandler.class),
                objectFactory.newInstance(ProjectHandler.class, settings),
                namer -> new VariantProjectDescriptorFactory(settings, namer)
        );
    }

    BuildAspects(AspectHandler aspectHandler, ProjectHandler projectHandler,
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

    public AspectHandler getAspects() {
        return aspectHandler;
    }

    public void aspects(Action<? super AspectHandler> action) {
        action.execute(aspectHandler);
    }

    public ProjectHandler getProjects() {
        return projectHandler;
    }

    public void projects(Action<? super ProjectHandler> action) {
        action.execute(projectHandler);
    }

    public void setProjectNamer(Namer<ParentVariantProjectDescriptor> namer) {
        variantProjectBuilderOnetimeFactory.setSource(namer);
    }

    public void setProjectNamer(Closure<String> namer) {
        setProjectNamer(namer::call);
    }

    public Set<VariantProjectDescriptor> getVariantProjects() {
        return Collections.unmodifiableSet(variantProjectDescriptors);
    }
}
