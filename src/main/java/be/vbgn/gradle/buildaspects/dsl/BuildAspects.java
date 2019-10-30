package be.vbgn.gradle.buildaspects.dsl;

import be.vbgn.gradle.buildaspects.aspect.AspectHandler;
import groovy.lang.Closure;
import javax.inject.Inject;
import org.gradle.api.Action;
import org.gradle.api.model.ObjectFactory;
import org.gradle.util.ConfigureUtil;

public class BuildAspects {
    private AspectHandler aspectHandler;

    @Inject
    public BuildAspects(ObjectFactory objectFactory) {
        aspectHandler = objectFactory.newInstance(AspectHandler.class);
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


}
