package be.vbgn.gradle.buildaspects.project;

import be.vbgn.gradle.buildaspects.component.Component;
import org.gradle.api.Namer;
import org.gradle.api.initialization.ProjectDescriptor;
import org.gradle.api.initialization.Settings;

public class ComponentProjectFactory {

    private final Settings settings;
    private final Namer<? super ComponentProjectDescriptor> projectNamer;

    public ComponentProjectFactory(Settings settings, Namer<? super ComponentProjectDescriptor> projectNamer) {
        this.settings = settings;
        this.projectNamer = projectNamer;
    }

    public ComponentProject createProject(ProjectDescriptor parentDescriptor, Component component) {
        ComponentProjectDescriptor componentProjectDescriptor = new ComponentProjectDescriptor(parentDescriptor,
                component);
        String projectName = projectNamer.determineName(componentProjectDescriptor);
        String projectPath = parentDescriptor.getPath();
        if(!projectPath.endsWith(":")) {
            projectPath+=":";
        }
        projectPath+=projectName;
        settings.include(projectPath);
        ProjectDescriptor projectDescriptor = settings.project(projectPath);
        return new ComponentProject(parentDescriptor, component, projectDescriptor);
    }

}
