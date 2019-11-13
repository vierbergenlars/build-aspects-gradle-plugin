package be.vbgn.gradle.buildaspects.variant;

import be.vbgn.gradle.buildaspects.aspect.Aspect;
import be.vbgn.gradle.buildaspects.aspect.Property;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class VariantBuilder {

    private VariantBuilderInternal builder = new VariantBuilderInternal();

    public void addAspect(Aspect<?> aspect) {
        builder = builder.addAspect(aspect);
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
            List<VariantImpl> variants = new ArrayList<>(this.variants.size() * aspect.getProperties().size());
            for (VariantImpl variant : this.variants) {
                for (Property<?> property : aspect.getProperties()) {
                    variants.add(variant.withProperty(property));
                }
            }

            return new VariantBuilderInternal(variants);
        }

        public List<? extends Variant> build() {
            return variants;
        }

    }
}
