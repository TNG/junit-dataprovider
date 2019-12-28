plugins {
    id("de.aaschmid.cpd") version "3.1"

    jacoco
    id("com.github.kt3k.coveralls") version "2.9.0"
}

val isBuildOnJenkins by extra(System.getenv().getOrDefault("BUILD_TAG", "").startsWith("jenkins-"))
println("buildOnJenkins = $isBuildOnJenkins for current build.")

val skipSpotBugs by extra(hasProperty("skipSpotBugs"))
println("Using skipSpotBugs = $skipSpotBugs for current build.")

// set default junit versions if not set via command line
val deps by extra(Dependencies(
        findProperty("junit4Version")?.toString() ?: "4.12",
        findProperty("junitJupiterVersion")?.toString() ?: "5.5.2"
))

group = "com.tngtech.junit.dataprovider"
version = "2.7-SNAPSHOT"

buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"
}

repositories {
    mavenCentral()
}
// -- CPD (= copy-paste-detection) plugin configuration ---------------------------------------------------------------
cpd {
    // Use Java 8 and later to execute cpd successfully
    toolVersion = "6.13.0"
}

tasks.cpdCheck {
    ignoreFailures = true
    minimumTokenCount = 25
    setSource(files(
            // only check java source code
            subprojects.flatMap { it.the<SourceSetContainer>()["main"].java.srcDirs },
            subprojects.flatMap { it.the<SourceSetContainer>()["test"].java.srcDirs }
    ))
}

subprojects {
    afterEvaluate {
        tasks.named("check") {
            dependsOn(rootProject.tasks["cpdCheck"])
        }
    }
}

// -- coveralls plugin multi-module project workaround ----------------------------------------------------------------
// Note: workaround because sub-projects are evaluated / configured after rootProject
// Attention: does not work with "configuration-on-demand" activated
gradle.projectsEvaluated {
    val publishedProjects = subprojects.filter { true }

    val jacocoMerge = tasks.register("jacocoMerge", JacocoMerge::class) {
        doFirst {
            executionData = files(executionData.filter { it.exists() })
        }
        publishedProjects.forEach { executionData(it.tasks.withType(Test::class)) }
        dependsOn(publishedProjects.flatMap { it.tasks.withType(Test::class) })
    }

    val jacocoRootReport = tasks.register("jacocoRootReport", JacocoReport::class) {
        description = "Generates an aggregate report from all subprojects"

        additionalSourceDirs.from(publishedProjects.flatMap { it.the<SourceSetContainer>()["main"].allSource.srcDirs })
        sourceDirectories.from(publishedProjects.flatMap { it.the<SourceSetContainer>()["main"].allSource.srcDirs })
        classDirectories.from(publishedProjects.flatMap {
            it.the<SourceSetContainer>()["main"].output.asFileTree.matching {
                // exclude FQDN duplicates -- both are annotations and therefore mostly irrelevant for coverage
                exclude("com/tngtech/junit/dataprovider/DataProvider.class")
                exclude("com/tngtech/junit/dataprovider/UseDataProvider.class")
            }
        })
        executionData(jacocoMerge.get().destinationFile)

        reports {
            xml.isEnabled = true // required by coveralls
        }
        dependsOn(jacocoMerge)
    }

    coveralls {
        jacocoReportPath = jacocoRootReport.get().reports.xml.destination
        sourceDirs = publishedProjects.flatMap { it.the<SourceSetContainer>()["main"].allSource.srcDirs }.map { it.absolutePath }
    }
}
