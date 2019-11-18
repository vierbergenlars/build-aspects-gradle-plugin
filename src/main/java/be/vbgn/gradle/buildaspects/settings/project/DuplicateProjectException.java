package be.vbgn.gradle.buildaspects.settings.project;

import org.gradle.api.initialization.ProjectDescriptor;

public final class DuplicateProjectException extends RuntimeException {

    private DuplicateProjectException(String message) {
        super(message);
    }

    private DuplicateProjectException(String message, Throwable cause) {
        super(message, cause);
    }

    static DuplicateProjectException forProject(ProjectDescriptor projectDescriptor) {
        return new DuplicateProjectException(
                "The project " + projectDescriptor.getPath() + " has already been registered.");
    }

    public static DuplicateProjectException forProjectInOtherBuildAspects(ProjectDescriptor projectDescriptor) {
        return new DuplicateProjectException(
                "The project has already been registered in an other buildAspects configuration.",
                forProject(projectDescriptor));
    }
}
