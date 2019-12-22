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

val isBuildOnJenkins by extra(System.getenv().getOrDefault("BUILD_TAG", "").startsWith("jenkins-"))
println("buildOnJenkins = $isBuildOnJenkins for current build.")

val skipSpotBugs by extra(hasProperty("skipSpotBugs"))
println("Using skipSpotBugs = $skipSpotBugs for current build.")

// set default junit versions if not set via command line
val junit4Version by extra(findProperty("junit4Version")?.toString() ?: "4.12")
println("Using JUnit4 version $junit4Version for current build.")
val junitJupiterVersion by extra(findProperty("junitJupiterVersion")?.toString() ?: "5.5.2")
println("Using JUnit Jupiter version $junitJupiterVersion for current build.")

buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"
}

allprojects {
    repositories {
        mavenCentral()
    }
}

apply(from = "legacy.build.gradle")
