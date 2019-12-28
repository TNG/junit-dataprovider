plugins {
    groovy
}

val isBuildOnJenkins: Boolean by rootProject.extra
val deps: Dependencies by rootProject.extra

sourceSets {
    register("integTest") {
        compileClasspath += named("main").get().output
        runtimeClasspath += named("main").get().output
    }
}

configurations {
    "integTestImplementation" {
        extendsFrom(configurations["testImplementation"])
    }
}

dependencies {
    "integTestImplementation"(deps.groovy)
}

tasks {
    val integTest = register<Test>("integTest") {
        group = "verification"
        description = "Runs all integration tests."

        ignoreFailures = isBuildOnJenkins

        classpath = sourceSets["integTest"].runtimeClasspath
        testClassesDirs = sourceSets["integTest"].output.classesDirs

        dependsOn(named("integTestClasses"))
    }

    val touchIntegTestResultsForJenkins = register<TouchTestResults>("touchIntegTestResultsForJenkins") {
        tasks(integTest)
        enabled = isBuildOnJenkins
    }

    named("build") {
        dependsOn(touchIntegTestResultsForJenkins)
    }
}
