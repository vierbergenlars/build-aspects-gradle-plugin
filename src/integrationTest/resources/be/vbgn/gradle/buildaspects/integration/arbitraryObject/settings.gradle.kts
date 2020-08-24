buildscript {
    repositories {
        mavenLocal()
    }
    dependencies {
        classpath("be.vbgn.gradle:build-aspects-plugin:${System.getProperty("pluginVersion")}")
    }

}
apply(plugin="be.vbgn.build-aspects")

rootProject.name = "arbitraryObject"

class SystemVersion public constructor(val systemVersion: String, val databaseVersion: String) {
    override fun toString(): String {
        return systemVersion+"-"+databaseVersion;
    }
}

configure<be.vbgn.gradle.buildaspects.settings.dsl.BuildAspects> {
    aspects {
        create("systemVersion", SystemVersion::class.java) {
            add(SystemVersion("1.0", "1.2"))
            add(SystemVersion("1.0", "1.3"))
            add(SystemVersion("2.0", "1.3"))
        }
        create("communityEdition", Boolean::class.javaObjectType, listOf(true, false))
        calculated<String>("communtiyString") {
            when(it.getProperty("communityEdition")) {
                true -> "community"
                else -> "enterprise"
            }
        }
    }
    projects {
        project(":")
    }
}
