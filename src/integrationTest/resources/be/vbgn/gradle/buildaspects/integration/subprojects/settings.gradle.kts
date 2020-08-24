pluginManagement {
    repositories {
        mavenLocal()
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "be.vbgn.build-aspects" && requested.version == "system") {
                useVersion(System.getProperty("pluginVersion"))
            }
        }
    }
}

plugins {
    id("be.vbgn.build-aspects") version "system"
}

rootProject.name = "subprojects"

configure<be.vbgn.gradle.buildaspects.settings.dsl.BuildAspects> {
    aspects {
        create("systemVersion", "1.0", "2.0")
        calculated("systemVersionFloat") {
            (it.getProperty("systemVersion") as String).toDouble()
        }
    }
    projects {
        include(":moduleA", ":systemB:moduleB")
    }
}
include(":systemB:moduleB:unmapped-subproject")
