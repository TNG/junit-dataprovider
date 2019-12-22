buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("biz.aQute.bnd:biz.aQute.bnd.gradle:4.3.1")
    }
}
plugins {
    id("com.github.spotbugs") version "3.0.0" apply false
    id("de.aaschmid.cpd") version "3.1"

    jacoco
    id("com.github.kt3k.coveralls") version "2.9.0"
}

apply(from = "legacy.build.gradle")
