package be.vbgn.gradle.buildaspects.settings.dsl;

import be.vbgn.gradle.buildaspects.aspect.AspectHandler;
import be.vbgn.gradle.buildaspects.settings.project.ParentVariantProjectDescriptor;
import be.vbgn.gradle.buildaspects.settings.project.ProjectHandler;
import be.vbgn.gradle.buildaspects.settings.project.VariantProjectDescriptor;
import groovy.lang.Closure;
import java.util.Set;
import java.util.function.Predicate;
import org.gradle.api.Action;
import org.gradle.api.Namer;
import org.gradle.api.plugins.ExtensionAware;

public interface BuildAspects extends ExtensionAware {

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
        setProjectNamer(p -> namer.rehydrate(p, namer.getOwner(), namer.getThisObject()).call(p));
    }

    void exclude(Predicate<ParentVariantProjectDescriptor> excluder);

    default void exclude(Closure<Boolean> excluder) {
        exclude(p -> excluder.rehydrate(p, excluder.getOwner(), excluder.getThisObject()).call(p));
    }

    Set<VariantProjectDescriptor> getVariantProjects();
}
