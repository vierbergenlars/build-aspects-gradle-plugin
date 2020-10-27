buildscript {
    repositories {
        mavenLocal()
    }
    dependencies {
        classpath("be.vbgn.gradle:build-aspects-plugin:${System.getProperty("pluginVersion")}")
    }

}
apply(plugin="be.vbgn.build-aspects")

rootProject.name = "nestedConfiguration"

the<be.vbgn.gradle.buildaspects.settings.dsl.BuildAspectsRoot>().nested {
    aspects {
        create("systemVersion", String::class.java) {
            add("1.0")
            add("2.0")
        }
        create("communityEdition", Boolean::class.javaObjectType, listOf(true, false))
    }
    projects {
        include(":moduleA")
    }
}
the<be.vbgn.gradle.buildaspects.settings.dsl.BuildAspectsRoot>().nested {
    aspects {
        create("systemVersion", String::class.java) {
            add("2.0")
            add("2.1")
        }
    }
    projects {
        include(":moduleB")
    }
}
