package be.vbgn.gradle.buildaspects.variant;

import be.vbgn.gradle.buildaspects.aspect.Aspect;
import be.vbgn.gradle.buildaspects.aspect.CalculatedPropertyBuilder;
import be.vbgn.gradle.buildaspects.aspect.Property;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class VariantBuilder {

    private VariantBuilderInternal builder = new VariantBuilderInternal();

    public void addAspect(Aspect<?> aspect) {
        builder = builder.addAspect(aspect);
    }

    public void addCalculatedPropertyBuilder(
            CalculatedPropertyBuilder<?> calculatedPropertyBuilder) {
        builder = builder.addCalculatedPropertyBuilder(calculatedPropertyBuilder);
    }

    public Collection<? extends Variant> getVariants() {
        return builder.build();
    }

    private static class VariantBuilderInternal {

        private final List<VariantImpl> variants;

        public VariantBuilderInternal() {
            this(Collections.singletonList(new VariantImpl(Collections.emptyList())));
        }

        private VariantBuilderInternal(List<VariantImpl> variants) {
            this.variants = Collections.unmodifiableList(variants);
        }

        public VariantBuilderInternal addAspect(Aspect<?> aspect) {
            List<VariantImpl> newVariants = new ArrayList<>(variants.size() * aspect.getProperties().size());
            for (VariantImpl variant : variants) {
                for (Property<?> property : aspect.getProperties()) {
                    newVariants.add(variant.withProperty(property));
                }
            }

            return new VariantBuilderInternal(newVariants);
        }

        public List<? extends Variant> build() {
            return variants;
        }

        public VariantBuilderInternal addCalculatedPropertyBuilder(
                CalculatedPropertyBuilder<?> calculatedPropertyBuilder) {
            return new VariantBuilderInternal(variants.stream()
                    .map(v -> v.withProperty(calculatedPropertyBuilder.build(v)))
                    .collect(Collectors.toList()));
        }
    }
}
