package be.vbgn.gradle.buildaspects.project.project;

import be.vbgn.gradle.buildaspects.variant.Variant;
import org.gradle.api.Project;

public interface VariantProject {

    Project getProject();

    Variant getVariant();
}
