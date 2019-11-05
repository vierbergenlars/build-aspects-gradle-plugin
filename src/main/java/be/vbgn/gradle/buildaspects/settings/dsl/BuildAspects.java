package be.vbgn.gradle.buildaspects.settings.dsl;

import be.vbgn.gradle.buildaspects.aspect.AspectHandler;
import be.vbgn.gradle.buildaspects.component.Component;
import be.vbgn.gradle.buildaspects.component.ComponentBuilder;
import be.vbgn.gradle.buildaspects.internal.OnetimeFactory;
import be.vbgn.gradle.buildaspects.settings.project.ComponentProject;
import be.vbgn.gradle.buildaspects.settings.project.ComponentProjectDescriptor;
import be.vbgn.gradle.buildaspects.settings.project.ComponentProjectFactory;
import be.vbgn.gradle.buildaspects.settings.project.DefaultComponentProjectNamer;
import be.vbgn.gradle.buildaspects.settings.project.ProjectHandler;
import groovy.lang.Closure;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import javax.inject.Inject;
import org.gradle.api.Action;
import org.gradle.api.Namer;
import org.gradle.api.NonNullApi;
import org.gradle.api.initialization.Settings;
import org.gradle.api.model.ObjectFactory;
import org.gradle.util.ConfigureUtil;

@NonNullApi
public class BuildAspects {

    private final AspectHandler aspectHandler;
    private final ProjectHandler projectHandler;


    private final Set<ComponentProject> componentProjects = new HashSet<>();
    private final OnetimeFactory<Namer<ComponentProjectDescriptor>, ComponentProjectFactory> componentProjectBuilderOnetimeFactory;

    @Inject
    public BuildAspects(ObjectFactory objectFactory, Settings settings) {
        this(
                objectFactory.newInstance(AspectHandler.class),
                objectFactory.newInstance(ProjectHandler.class, settings),
                namer -> new ComponentProjectFactory(settings, namer)
        );
    }

    BuildAspects(AspectHandler aspectHandler, ProjectHandler projectHandler,
            Function<Namer<ComponentProjectDescriptor>, ComponentProjectFactory> componentProjectFactoryFactory) {
        this.aspectHandler = aspectHandler;
        this.projectHandler = projectHandler;
        componentProjectBuilderOnetimeFactory = new OnetimeFactory<>(componentProjectFactoryFactory);
        componentProjectBuilderOnetimeFactory.setSource(new DefaultComponentProjectNamer());
        ComponentBuilder componentBuilder = new ComponentBuilder();

        aspectHandler.aspectAdded(componentBuilder::addAspect);
        aspectHandler.aspectAdded(a -> {
            if (!projectHandler.getProjects().isEmpty()) {
                throw new IllegalStateException("You can not modify aspects after projects have been registered.");
            }
        });
        projectHandler.projectAdded(projectDescriptor -> {
            for (Component component : componentBuilder.getComponents()) {
                componentProjects.add(componentProjectBuilderOnetimeFactory.build().createProject(projectDescriptor, component));
            }
        });
    }

    public AspectHandler getAspects() {
        return aspectHandler;
    }

    public void aspects(Action<? super AspectHandler> action) {
        action.execute(aspectHandler);
    }

    public void aspects(Closure action) {
        aspects(ConfigureUtil.configureUsing(action));
    }

    public ProjectHandler getProjects() {
        return projectHandler;
    }

    public void projects(Action<? super ProjectHandler> action) {
        action.execute(projectHandler);
    }

    public void projects(Closure action) {
        projects(ConfigureUtil.configureUsing(action));
    }

    public void projectNamer(Namer<ComponentProjectDescriptor> namer) {
        componentProjectBuilderOnetimeFactory.setSource(namer);
    }

    public void projectNamer(Closure<String> namer) {
        projectNamer(namer::call);
    }

    public Set<ComponentProject> getComponentProjects() {
        return Collections.unmodifiableSet(componentProjects);
    }
}
