plugins {
    id("be.vbgn.build-aspects")
}

var system1ProjectCalled = false
var system3ProjectCalled = false
buildAspects.withVariant("systemVersion", "1.0") {
    system1ProjectCalled = true
    assert(name == "moduleA-systemVersion-1.0")
    val moduleASystemVersion1 by tasks.registering
    tasks.named("clean") {
        dependsOn(moduleASystemVersion1)
    }
}

buildAspects.withVariant("systemVersion", "3.0") {
    system3ProjectCalled = true
}

afterEvaluate {
    assert(system1ProjectCalled)
    assert(!system3ProjectCalled)
}

buildAspects.subprojects {
    val buildVariant = the<be.vbgn.gradle.buildaspects.variant.Variant>();
    val siblingProject = the<be.vbgn.gradle.buildaspects.project.dsl.ProjectExtension>().findProject(":systemB:moduleB", buildVariant);
    assert(siblingProject != null)
    assert(siblingProject!!.the<be.vbgn.gradle.buildaspects.variant.Variant>().properties == buildVariant.properties)
}

buildAspects.withVariant("systemVersion", "1.0") {
    val buildVariant = the<be.vbgn.gradle.buildaspects.variant.Variant>();
    val ext = the<be.vbgn.gradle.buildaspects.project.dsl.ProjectExtension>();
    assert(ext.project(":systemB:moduleB", buildVariant).path == ":systemB:moduleB:moduleB-systemVersion-1.0")
    assert(ext.variantTask(":systemB:moduleB", buildVariant, "clean") == ":systemB:moduleB:moduleB-systemVersion-1.0:clean")
}

buildAspects.subprojects {
    val buildVariant = the<be.vbgn.gradle.buildaspects.variant.Variant>();
    assert(buildVariant.getProperty<Double>("systemVersionFloat") == buildVariant.getProperty<String>("systemVersion").toDouble())
}
