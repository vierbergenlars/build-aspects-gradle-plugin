plugins {
    id("be.vbgn.build-aspects")
}

subprojects {
    if (name == "moduleB-systemVersion-1.0" || name == "moduleB-systemVersion-2.0") {
        assert(project.extensions.findByName("buildAspects") != null)
        assert(project.extensions.findByName("buildVariant") != null)
    } else {
        assert(project.extensions.findByName("buildAspects") == null)
        assert(project.extensions.findByName("buildVariant") == null)
    }
}

buildAspects.withVariant("systemVersion", "1.0") {
    val buildVariant = the<be.vbgn.gradle.buildaspects.variant.Variant>();
    assert(buildVariant.getProperty<String>("systemVersion") == "1.0")
    assert(buildVariant.getProperty<String>("systemVersion") == "1.0")
}

buildAspects.withVariant({ it.getProperty<String>("systemVersion") == "2.0" }) {
    val buildVariant = the<be.vbgn.gradle.buildaspects.variant.Variant>();
    assert(buildVariant.getProperty<String>("systemVersion") == "2.0")
    try {
        assert(buildVariant.getProperty<String>("nonexistingProperty") != null, {"Exception should have been thrown for nonexisting property"})
        assert(false, {"Exception should have been thrown for nonexisting property"})
    } catch (_: groovy.lang.MissingPropertyException) {

    }
}
