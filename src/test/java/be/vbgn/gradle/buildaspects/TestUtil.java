package be.vbgn.gradle.buildaspects;

import be.vbgn.gradle.buildaspects.aspect.AspectHandler;
import be.vbgn.gradle.buildaspects.variant.Variant;
import be.vbgn.gradle.buildaspects.variant.VariantBuilder;
import java.util.Map;

public class TestUtil {

    public static Variant createVariant(Map<String, ?>... settings) {
        VariantBuilder variantBuilder = new VariantBuilder();
        AspectHandler aspectHandler = new AspectHandler();
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
