package be.vbgn.gradle.buildaspects.settings.dsl;

public final class IllegalBuildAspectsStateException extends IllegalStateException {

    private IllegalBuildAspectsStateException(String s) {
        super(s);
    }

    static IllegalBuildAspectsStateException nestedAndRootConfiguration() {
        return new IllegalBuildAspectsStateException(
                "Nested BuildAspects configurations can not be combined with configuration of the root BuildAspects configurations.");
    }

    static IllegalBuildAspectsStateException modifyAspectsAfterProjects() {
        return new IllegalBuildAspectsStateException("You can not modify aspects after projects have been registered.");
    }

    static IllegalBuildAspectsStateException modifyNamerAfterProjects() {
        return new IllegalBuildAspectsStateException(
                "You can not modify the projectNamer after projects have been registered.");
    }
}
