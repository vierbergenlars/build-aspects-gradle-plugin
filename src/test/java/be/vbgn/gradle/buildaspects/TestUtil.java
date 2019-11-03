package be.vbgn.gradle.buildaspects;

import be.vbgn.gradle.buildaspects.aspect.Aspect;
import be.vbgn.gradle.buildaspects.aspect.AspectHandler;
import be.vbgn.gradle.buildaspects.aspect.Component;
import java.util.Map;

public class TestUtil {

    public static Component createComponent(Map<String, ?> ...settings) {
        AspectHandler aspectHandler = new AspectHandler();
        for (Map<String, ?> setting : settings) {
            setting.forEach((name, value) -> {
                Aspect<Object> aspect = aspectHandler.create(name, Object.class);
                aspect.add(value);
            });
        }
        return aspectHandler.getComponents().stream().findFirst().get();
    }
}
