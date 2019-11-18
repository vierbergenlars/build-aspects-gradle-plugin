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
        return modifyAfterProjectsAdded("aspects");
    }

    static IllegalBuildAspectsStateException modifyNamerAfterProjects() {
        return modifyAfterProjectsAdded("projectNamer");
    }

    static IllegalBuildAspectsStateException modifyExcludeAfterProjects() {
        return modifyAfterProjectsAdded("exclude");
    }

    private static IllegalBuildAspectsStateException modifyAfterProjectsAdded(String property) {
        return new IllegalBuildAspectsStateException(
                "You can not modify \""+property+"\" after projects have been registered.");

    }
}
