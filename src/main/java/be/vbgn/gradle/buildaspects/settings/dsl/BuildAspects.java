package be.vbgn.gradle.buildaspects.settings.dsl;

import be.vbgn.gradle.buildaspects.aspect.AspectHandler;
import be.vbgn.gradle.buildaspects.settings.project.ParentVariantProjectDescriptor;
import be.vbgn.gradle.buildaspects.settings.project.ProjectHandler;
import be.vbgn.gradle.buildaspects.settings.project.VariantProjectDescriptor;
import groovy.lang.Closure;
import java.util.Set;
import org.gradle.api.Action;
import org.gradle.api.Namer;

public interface BuildAspects {

    AspectHandler getAspects();

    default void aspects(Action<? super AspectHandler> action) {
        action.execute(getAspects());
    }

    ProjectHandler getProjects();

    default void projects(Action<? super ProjectHandler> action) {
        action.execute(getProjects());
    }

    void setProjectNamer(Namer<ParentVariantProjectDescriptor> namer);

    default void setProjectNamer(Closure<String> namer) {
        setProjectNamer(namer::call);
    }

    Set<VariantProjectDescriptor> getVariantProjects();
}
