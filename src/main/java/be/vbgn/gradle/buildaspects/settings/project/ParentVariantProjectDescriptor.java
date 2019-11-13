package be.vbgn.gradle.buildaspects.settings.project;

import be.vbgn.gradle.buildaspects.variant.Variant;
import org.gradle.api.initialization.ProjectDescriptor;

public class ParentVariantProjectDescriptor {

    private final ProjectDescriptor parentProjectDescriptor;
    private final Variant variant;

    ParentVariantProjectDescriptor(ProjectDescriptor parentProjectDescriptor, Variant variant) {
        this.parentProjectDescriptor = parentProjectDescriptor;
        this.variant = variant;
    }

    public Variant getVariant() {
        return variant;
    }

    public ProjectDescriptor getParentProjectDescriptor() {
        return parentProjectDescriptor;
    }
}
