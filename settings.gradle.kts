pluginManagement {
    repositories {
        mavenCentral() // required as first repo for Bnd Gradle Plugins, see also https://github.com/bndtools/bnd/issues/3174
        gradlePluginPortal()
    }
}
plugins {
    id("com.gradle.enterprise").version("3.1.1")
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
include(":core", ":junit4", ":junit-jupiter", ":junit-jupiter-params")
