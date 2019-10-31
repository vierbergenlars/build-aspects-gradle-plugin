package be.vbgn.gradle.buildaspects.dsl;

import be.vbgn.gradle.buildaspects.aspect.AspectHandler;
import be.vbgn.gradle.buildaspects.aspect.Component;
import be.vbgn.gradle.buildaspects.internal.OnetimeFactory;
import be.vbgn.gradle.buildaspects.project.ComponentProjectFactory;
import be.vbgn.gradle.buildaspects.project.ComponentProjectDescriptor;
import be.vbgn.gradle.buildaspects.project.ProjectHandler;
import groovy.lang.Closure;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.gradle.api.Action;
import org.gradle.api.Namer;
import org.gradle.api.initialization.Settings;
import org.gradle.api.model.ObjectFactory;
import org.gradle.util.ConfigureUtil;

public class BuildAspects {

    private static class DefaultComponentProjectNamer implements Namer<ComponentProjectDescriptor> {

        @Override
        public String determineName(ComponentProjectDescriptor object) {
            String parentName = object.getParentProjectDescriptor().getName();
            String propertiesName = object.getComponent().getProperties().stream()
                    .map(p -> p.getName() + "-" + p.getValue()).collect(
                            Collectors.joining("-"));
            return parentName + "-" + propertiesName;
        }
    }

    private AspectHandler aspectHandler;
    private ProjectHandler projectHandler;
    private Namer<ComponentProjectDescriptor> projectNamer = new DefaultComponentProjectNamer();
    private OnetimeFactory<Namer<ComponentProjectDescriptor>, ComponentProjectFactory> componentProjectBuilderOnetimeFactory;

    @Inject
    public BuildAspects(ObjectFactory objectFactory, Settings settings) {
        aspectHandler = objectFactory.newInstance(AspectHandler.class);
        projectHandler = objectFactory.newInstance(ProjectHandler.class, settings);
        componentProjectBuilderOnetimeFactory = new OnetimeFactory<>(namer -> new ComponentProjectFactory(settings, namer));

        aspectHandler.aspectAdded(a -> {
            if(!projectHandler.getProjects().isEmpty()) {
                throw new IllegalStateException("You can not modify aspects after projects have been registered.");
            }
        });
        projectHandler.projectAdded(projectDescriptor -> {
            for (Component component : aspectHandler.getComponents()) {
                componentProjectBuilderOnetimeFactory.build().createProject(projectDescriptor, component);
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
}
