package be.vbgn.gradle.buildaspects.settings.dsl;

public class IllegalBuildAspectsStateException extends IllegalStateException {

    public IllegalBuildAspectsStateException() {
    }

    public IllegalBuildAspectsStateException(String s) {
        super(s);
    }

    public IllegalBuildAspectsStateException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalBuildAspectsStateException(Throwable cause) {
        super(cause);
    }

    static IllegalBuildAspectsStateException nestedAndRootConfiguration() {
        return new IllegalBuildAspectsStateException("Nested BuildAspects configurations can not be combined with configuration of the root BuildAspects configurations.");
    }

    static IllegalBuildAspectsStateException modifyAspectsAfterProjects() {
        return new IllegalBuildAspectsStateException("You can not modify aspects after projects have been registered.");
    }

    static IllegalBuildAspectsStateException modifyNamerAfterProjects() {
        return new IllegalBuildAspectsStateException("You can not modify the projectNamer after projects have been registered.");
    }
}
