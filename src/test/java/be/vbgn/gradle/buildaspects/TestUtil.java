package be.vbgn.gradle.buildaspects;

import be.vbgn.gradle.buildaspects.aspect.AspectHandler;
import be.vbgn.gradle.buildaspects.variant.Variant;
import be.vbgn.gradle.buildaspects.variant.VariantBuilder;
import java.util.Map;
import org.gradle.api.plugins.ExtensionContainer;

public class TestUtil {

    public static AspectHandler createAspectHandler() {
        return new AspectHandler() {
            @Override
            public ExtensionContainer getExtensions() {
                return null;
            }
        };
    }

    public static Variant createVariant(Map<String, ?>... settings) {
        VariantBuilder variantBuilder = new VariantBuilder();
        AspectHandler aspectHandler = createAspectHandler();
        aspectHandler.aspectAdded(variantBuilder::addAspect);
        for (Map<String, ?> setting : settings) {
            setting.forEach((name, value) -> {
                aspectHandler.create(name, Object.class, aspect -> {
                    aspect.add(value);
                });
            });
        }
        return variantBuilder.getVariants().stream().findFirst().get();
    }
}
