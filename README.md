# Build aspects plugin [![Build Status](https://travis-ci.com/vierbergenlars/build-aspects-gradle-plugin.svg?branch=master)](https://travis-ci.com/vierbergenlars/build-aspects-gradle-plugin) [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=vierbergenlars_build-aspects-gradle-plugin&metric=alert_status)](https://sonarcloud.io/dashboard?id=vierbergenlars_build-aspects-gradle-plugin)

A gradle plugin that manages different variants for your project.

<details>
<summary>
When do I need this plugin?
</summary>

When your `settings.gradle` file looks like this, you might be a developer that maintains an extension for multiple non-backwards compatible versions of a platform.

```groovy
include ':projectA'
include ':projectA:projectA-1.0-community'
include ':projectA:projectA-1.0-enterprise'
include ':projectA:projectA-2.0-community'
include ':projectA:projectA-2.0-enterprise'
include ':projectB'
include ':projectB:projectB-1.0-community'
include ':projectB:projectB-1.0-enterprise'
include ':projectB:projectB-2.0-community'
include ':projectB:projectB-2.0-enterprise'
```

In your `build.gradle`, you have probably extracted out common parts to the parent project.
You now have to resort to parsing project names to determine which variant you are creating, or duplicate this knowledge in each subproject.

This plugin can solve these problems by automatically giving you access to the variants you are building in each project.

Your new `settings.gradle` looks like this:

```groovy
buildAspects { 
    aspects {
        create('systemVersion', '1.0', '2.0')
        create('systemEdition', 'community', 'enterprise')
    }
    projects {
        include ':projectA'
        include ':projectB'
    }
}
```

Your `build.gradle` may look like this:

```groovy
// projectA/build.gradle
subprojects {
    // Common code for all subprojects
    if(buildVariant.systemEdition == "community") {
        // Some special case for community projects
    }
    dependencies {
        compileOnly "org.example:system:system-api:${buildVariant.systemVersion}"
    }
}
```
</details>

## Installation

This plugin is a `Settings` plugin. It is applied to `settings.gradle` instead of in `build.gradle` and affects your whole build.

Starting from Gradle 6, you can use a `plugins` block in  `settings.gradle` to install the plugin. 

```groovy
// settings.gradle
plugins {
    id "be.vbgn.build-aspects" version "1.0.0" // For latest version, check https://plugins.gradle.org/plugin/be.vbgn.build-aspects
}
```

<details>
<summary>Gradle 5 syntax</summary>

```groovy
// settings.gradle
buildscript {
    dependencies {
        classpath 'be.vbgn.gradle:build-aspects-plugin:1.0.0' // For latest version, check https://plugins.gradle.org/plugin/be.vbgn.build-aspects
    }
}
apply plugin: be.vbgn.gradle.buildaspects.BuildAspectsPlugin
```

</details>

## Usage

This plugin creates multiple variants of a build by creating sub-projects.
An aspect is a single variable that changes for these sub-projects.
Defining multiple aspects creates variant sub-projects for the cartesian product of all these aspects.

After applying the build-aspects plugin, you can define aspects and projects to which these aspects apply.

Note that, due to evaluation order requirements, it is not possible to create new aspects or modify existing aspects after a project has been added.

```groovy
// settings.gradle
include ':projectB'
buildAspects {
    aspects {
        // Create an aspect by specifying its name and all values
        create("aspectName", "value1", "value2", "...")
        // Or create the aspect more dynamically, by specifying its name and type
        // and then adding values inside a configuration closure
        create("otherAspect", Boolean) {
            add(true)
            add(false)
        }
    }
    projects {
        // Add an already included project to buildAspects
        project(':moduleB')
        // Include a new project in the build and add it to buildAspects
        include(':moduleA')
    }
}
```

After applying the plugin in `settings.gradle`, sub-projects will be created.
Typically, you want to keep those generated sub-projects code-less and configure them from their parent project.

To support this strategy, a `buildAspects` object is available in the parent projects and the generated sub-projects.
This object contains methods that help you to only apply configuration to some of the sub-projects.

```groovy
// moduleA/build.gradle
buildAspects.withVariant("aspectName", "value1") {
    // The context here is one of your sub-projects for which the aspect "aspectName" is "value1"
}
```

You can also get the value of an aspect for a subproject by using the `buildVariant` object.

```groovy
// moduleA/build.gradle
buildAspects.subprojects {
    println buildVariant.aspectName // One of "value1", "value2", "..." depending on which subproject you are working on.
}
```

## API

### `Settings` `buildAspects` configuration

The `buildAspects` configuration in `settings.gradle` configures the aspects and the projects it will affect.

#### `buildAspects.aspects {}`

The `buildAspects.aspects {}` closure creates aspects and sets their values.

There are multiple ways to define an aspect's values:

 * Inline as arguments: The aspect name as first argument, followed by all values. In this format, the type is automatically determined.
    ```groovy
    buildAspects.aspects {
       create("aspectName", "value1", "value2", "...")
    }
    ```
 * Inline as a list: The aspect name as first argument, followed by the type of the aspect values and then a list of all values.
    ```groovy
    buildAspects.aspects {
       create("aspectName", String, ["value1", "value2", "..."])
    }
    ```
 * In a closure: The aspect name and type of the aspect values, followed by a closure where values can be added.
    ```groovy
   buildAspects.aspects {
       create("aspectName", String) {
           add("value1")
           add("value2")
           add("...")
       }
   }
    ```

It is not allowed to create multiple aspects with the same name, and aspects are applied to projects in the same order as they are defined.

Aspect values are not limited to strings, you can use any object as aspect value.
Your custom class must implement a stable `toString()` method, which is used to generate the sub-project name.
The object should also be immutable, and should not change after it has been added to an aspect.

<details>
<summary>Example: aspect with a custom object</summary>

```groovy
// settings.gradle
class SystemVersion {
    final String systemVersion
    final String databaseVersion
    SystemVersion(String systemVersion, String databaseVersion) {
        this.systemVersion = systemVersion
        this.databaseVersion = databaseVersion
    }
    String toString() {
        return this.systemVersion+"-"+this.databaseVersion;
    }
}

buildAspects {
    aspects {
        create("systemVersion", SystemVersion) {
            add(new SystemVersion("1.0", "1.2"))
            add(new SystemVersion("1.0", "1.3"))
            add(new SystemVersion("2.0", "1.3"))
        }
    }
}
```

</details>

#### `buildAspects.projectNamer`

The `projectNamer` property is a `Namer<ParentVariantProjectDescriptor>` that derives the name of a generated sub-project from the parent project and the variant properties of the sub-project.

The default namer constructs the project name from the parent project and all aspect names and values, separated by `-`.

If you want to overwrite the `projectNamer`, you must do so before adding any projects to the `buildAspects` configuration.

#### `buildAspects.projects {}`

The `buildAspects.projects {}` closure registers projects to which aspects will be applied.

Projects are registered using the `project(String)` method, where the full path to the project is passed.
Projects that are registered this way have to exist already (i.e. they must already be included in the build using [`Settings#include()`](https://docs.gradle.org/current/dsl/org.gradle.api.initialization.Settings.html#org.gradle.api.initialization.Settings:include(java.lang.String[])))

```groovy
// settings.gradle
include ':moduleA'
buildAspects.projects {
    project(':moduleA')
}
```

It is also possible to include and register a project at the same time, using the `include()` method.
This will first include the project in the build and then register it with this plugin.

```groovy
// settings.gradle
buildAspects.projects {
    include ':moduleA'
}
```

### `buildAspects.nested {}`

When necessary, it is possible to create multiple, unrelated `buildAspects` configurations using the `buildAspects.nested {}` closure.
Every invocation of the closure creates a new `buildAspects` object, allowing you to create multiple distinct configurations.

Note that every project can only be registered with one `buildAspects` configuration.
The `buildAspects.nested {}` closure is only available on the root `buildAspects` configuration, and can not be nested themselves.

```groovy
// settings.gradle
buildAspects.nested {
    aspects {
        create("systemVersion", "1.0", "2.0") 
        create("communityEdition", true, false) 
    }
    projects {
        include(':moduleA')
    }
}

buildAspects.nested {
    aspects {
        create("systemVersion", "2.0", "2.1") 
    }
    projects {
        include(':moduleB')
    }
}
```

### `Project` `buildAspects` API

The `buildAspects` API is exposed in projects registered with the plugin and in generated sub-projects.

#### `buildAspects.when(Predicate<Variant>) {}`

The `when(Predicate<Variant>) {}` closure only executes on the sub-projects for which the predicate function returns true.
It is an easy way to only apply some configuration only to the variants for which it is applicable.

```groovy
// build.gradle
buildAspects.when({ it.getProperty("aspectName") == "value1"}) {
    // code to execute only for projects where aspect "aspectName" has value "value1"
    // The context inside this closure is one of the sub-projects that matched the predicate
}
```

#### `buildAspects.withVariant(String, Object) {}`

This closure is a specialized version of the `when(Predicate) {}` closure that will only execute its closure on sub-projects where a variant has a certain value.
It covers the common case of checking for one specific variant to make your buildscript more readable

```groovy
// build.gradle
buildAspects.withVariant("aspectName", "value1") {
    // code to execute only for projects where aspect "aspectName" has value "value1"
    // The context inside this closure is one of the sub-projects that matched the predicate
}
```

#### `buildAspects.subprojects {}`

The `buildAspects.subprojects{}` closure is similar to `project.subprojects {}`, but it only applies to generated sub-projects that are variants.

This might be helpful in the case that only some of the sub-projects of a registered project are variants, and other sub-projects exist that are not variants of the parent. 

### `Project` `buildVariant` API

The `buildVariant` API is only exposed in generated sub-projects.
It provides information about which variant this sub-project builds. 

It will usually be used when building specific sub-project configurations in an imperative way instead of a declarative way.

```groovy
// build.gradle
buildAspects.subprojects {
    if(buildVariant.aspectName == "value1") {
        // So something with this sub-project
    }
}
```


