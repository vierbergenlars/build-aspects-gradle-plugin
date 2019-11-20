package be.vbgn.gradle.buildaspects.project.project;

import be.vbgn.gradle.buildaspects.variant.Variant;
import java.util.Objects;
import org.gradle.api.Project;

public class VariantProjectImpl implements VariantProject {

    private final Variant variant;
    private final Project project;


    VariantProjectImpl(Project project, Variant variant) {
        this.variant = variant;
        this.project = project;
    }

    @Override
    public Project getProject() {
        return project;
    }

    @Override
    public Variant getVariant() {
        return variant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        VariantProject that = (VariantProject) o;
        return getVariant().equals(that.getVariant()) &&
                getProject().equals(that.getProject());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getVariant(), getProject());
    }

    @Override
    public String toString() {
        return "VariantProject{" + project + " (" + variant + ")}";
    }
}
