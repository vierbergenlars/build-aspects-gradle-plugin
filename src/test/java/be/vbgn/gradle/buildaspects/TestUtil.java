package be.vbgn.gradle.buildaspects;

import be.vbgn.gradle.buildaspects.aspect.AspectHandler;
import be.vbgn.gradle.buildaspects.component.Component;
import be.vbgn.gradle.buildaspects.component.ComponentBuilder;
import java.util.Map;

public class TestUtil {

    public static Component createComponent(Map<String, ?>... settings) {
        ComponentBuilder componentBuilder = new ComponentBuilder();
        AspectHandler aspectHandler = new AspectHandler();
        aspectHandler.aspectAdded(componentBuilder::addAspect);
        for (Map<String, ?> setting : settings) {
            setting.forEach((name, value) -> {
                aspectHandler.create(name, Object.class, aspect -> {
                    aspect.add(value);
                });
            });
        }
        return componentBuilder.getComponents().stream().findFirst().get();
    }
}
