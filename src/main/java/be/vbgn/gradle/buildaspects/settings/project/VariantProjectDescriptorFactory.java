package be.vbgn.gradle.buildaspects.settings.project;

import be.vbgn.gradle.buildaspects.variant.Variant;
import org.gradle.api.Namer;
import org.gradle.api.initialization.ProjectDescriptor;
import org.gradle.api.initialization.Settings;

public class VariantProjectDescriptorFactory {

    private final Settings settings;
    private final Namer<? super ParentVariantProjectDescriptor> projectNamer;

    public VariantProjectDescriptorFactory(Settings settings,
            Namer<? super ParentVariantProjectDescriptor> projectNamer) {
        this.settings = settings;
        this.projectNamer = projectNamer;
    }

    public VariantProjectDescriptor createProject(ProjectDescriptor parentDescriptor, Variant variant) {
        ParentVariantProjectDescriptor parentVariantProjectDescriptor = createParentProjectDescriptor(parentDescriptor,
                variant);
        String projectName = projectNamer.determineName(parentVariantProjectDescriptor);
        String projectPath = parentDescriptor.getPath();
        if (!projectPath.endsWith(":")) {
            projectPath += ":";
        }
        projectPath += projectName;
        settings.include(projectPath);
        ProjectDescriptor projectDescriptor = settings.project(projectPath);
        return new VariantProjectDescriptor(parentDescriptor, variant, projectDescriptor);
    }

    public ParentVariantProjectDescriptor createParentProjectDescriptor(ProjectDescriptor parentDescriptor,
            Variant variant) {
        return new ParentVariantProjectDescriptor(
                parentDescriptor,
                variant);
    }

}
