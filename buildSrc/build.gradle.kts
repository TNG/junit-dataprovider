plugins {
    kotlin("jvm") version "1.3.50"
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://plugins.gradle.org/m2/")
    }
}

dependencies {
    // external plugins
    implementation("com.github.spotbugs:spotbugs-gradle-plugin:3.0.0")
    implementation("biz.aQute.bnd:biz.aQute.bnd.gradle:4.3.1")
}
