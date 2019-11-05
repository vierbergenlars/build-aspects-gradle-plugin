package be.vbgn.gradle.buildaspects.settings.project;

import be.vbgn.gradle.buildaspects.component.Component;
import org.gradle.api.Namer;
import org.gradle.api.initialization.ProjectDescriptor;
import org.gradle.api.initialization.Settings;

public class ComponentProjectDescriptorFactory {

    private final Settings settings;
    private final Namer<? super ParentComponentProjectDescriptor> projectNamer;

    public ComponentProjectDescriptorFactory(Settings settings,
            Namer<? super ParentComponentProjectDescriptor> projectNamer) {
        this.settings = settings;
        this.projectNamer = projectNamer;
    }

    public ComponentProjectDescriptor createProject(ProjectDescriptor parentDescriptor, Component component) {
        ParentComponentProjectDescriptor parentComponentProjectDescriptor = new ParentComponentProjectDescriptor(
                parentDescriptor,
                component);
        String projectName = projectNamer.determineName(parentComponentProjectDescriptor);
        String projectPath = parentDescriptor.getPath();
        if (!projectPath.endsWith(":")) {
            projectPath += ":";
        }
        projectPath += projectName;
        settings.include(projectPath);
        ProjectDescriptor projectDescriptor = settings.project(projectPath);
        return new ComponentProjectDescriptor(parentDescriptor, component, projectDescriptor);
    }

}
