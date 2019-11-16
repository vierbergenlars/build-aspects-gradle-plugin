package be.vbgn.gradle.buildaspects.settings.project;

import org.gradle.api.initialization.ProjectDescriptor;

public class DuplicateProjectException extends RuntimeException {

    public DuplicateProjectException() {
    }

    public DuplicateProjectException(String message) {
        super(message);
    }

    public DuplicateProjectException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateProjectException(Throwable cause) {
        super(cause);
    }

    public static DuplicateProjectException forProject(ProjectDescriptor projectDescriptor) {
        return new DuplicateProjectException(
                "The project " + projectDescriptor.getPath() + " has already been registered.");
    }
}
