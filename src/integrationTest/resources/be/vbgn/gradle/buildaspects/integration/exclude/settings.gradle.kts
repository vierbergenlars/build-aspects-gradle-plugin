buildscript {
    repositories {
        mavenLocal()
    }
    dependencies {
        classpath("be.vbgn.gradle:build-aspects-plugin:${System.getProperty("pluginVersion")}")
    }

}
apply(plugin="be.vbgn.build-aspects")

rootProject.name = "exclude"

configure<be.vbgn.gradle.buildaspects.settings.dsl.BuildAspects> {
    aspects {
        create("systemVersion", String::class.java) {
            add("1.0")
            add("2.0")
        }
        create("communityEdition", Boolean::class.javaObjectType, listOf(true, false))
    }
    exclude { it.variant.getProperty("systemVersion") == "1.0" && it.variant.getProperty("communityEdition") == true }
    exclude { it.variant.getProperty("systemVersion") == "2.0" && it.variant.getProperty("communityEdition") == false }
    projects {
        project(":")
    }
}
