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

subprojects {
    apply<JacocoPlugin>()
    apply<JavaLibraryPlugin>()
    apply<com.github.spotbugs.SpotBugsPlugin>()
    apply<aQute.bnd.gradle.BndBuilderPlugin>()

    group = "com.tngtech.junit.dataprovider"
    version = "2.7-SNAPSHOT"

    dependencies {
        "compileOnly"("com.github.spotbugs:spotbugs-annotations:3.1.5")
        "testImplementation"("com.github.spotbugs:spotbugs-annotations:3.1.5")
    }

    configure<JavaPluginExtension> {
        withJavadocJar()
        withSourcesJar()
    }

    tasks {
        withType<JavaCompile> {
            options.encoding = "UTF-8"
            options.compilerArgs.addAll(listOf("-Xlint:all", "-Werror"))
        }

        withType<Jar> {
            from(project.rootDir) {
                include("LICENSE.md", "LICENSE-notice.md")
                into("META-INF")
            }
        }

        named<Javadoc>("javadoc") {
            if (JavaVersion.current().isJava9Compatible) {
                (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
            }
        }
    }
}

project(":core") {
    configure<BasePluginConvention> {
        archivesBaseName = "junit-dataprovider-core"
        description = "The common core for a TestNG like dataprovider runner for JUnit."
    }

    configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_1_6
        targetCompatibility = JavaVersion.VERSION_1_6
    }

    dependencies {
        "testImplementation"("junit:junit:4.12")

        "testImplementation"("org.assertj:assertj-core:1.7.1")
        "testImplementation"("org.mockito:mockito-core:2.18.3")
    }

    tasks {
        withType<JavaCompile> {
            options.compilerArgs.addAll(listOf("-Xlint:-options"))
        }

        named<Jar>("jar") {
            manifest {
                attributes(
                        "Automatic-Module-Name" to "com.tngtech.junit.dataprovider.core"
                )
            }
        }
    }
}

project(":junit4") {
    apply<GroovyPlugin>()

    configure<BasePluginConvention> {
        archivesBaseName = "junit4-dataprovider"
        description = "A TestNG like dataprovider runner for JUnit having a simplified syntax compared to all the existing JUnit4 features."
    }

    configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_1_6
        targetCompatibility = JavaVersion.VERSION_1_6
    }

    configure<SourceSetContainer> {
        create("integTest") {
            compileClasspath += named("main").get().output + named("test").get().output
            runtimeClasspath += named("main").get().output + named("test").get().output
        }
    }

    configurations {
        "integTestImplementation" {
            extendsFrom(configurations["testImplementation"])
        }
    }

    dependencies {
        "api"(project(":core"))
        "api"("junit:junit:${junit4Version}")

        "testImplementation"("org.assertj:assertj-core:1.7.1")
        "testImplementation"("org.mockito:mockito-core:2.18.3")

        "integTestImplementation"("org.codehaus.groovy:groovy:2.4.7")
    }

    tasks {
        withType<JavaCompile> {
            options.compilerArgs.addAll(listOf("-Xlint:-options"))
        }

        named<Jar>("jar") {
            manifest {
                attributes(
                        "Automatic-Module-Name" to "com.tngtech.junit.dataprovider.junit4"
                )
            }
        }

        val integTest = register<Test>("integTest") {
            group = "verification"
            description = "Runs all integration tests."

            ignoreFailures = isBuildOnJenkins

            classpath = project.the<SourceSetContainer>()["integTest"].runtimeClasspath
            testClassesDirs = project.the<SourceSetContainer>()["integTest"].output.classesDirs

            dependsOn(named("integTestClasses"))
        }
        val touchIntegTestResultsForJenkins = register<TouchTestResults>("touchIntegTestResultsForJenkins") {
            tasks(integTest)
            enabled = isBuildOnJenkins
        }
        getByName("build").dependsOn(touchIntegTestResultsForJenkins)
    }
}

// -- Custom tasks ------------------------------------------------------------
/**
 * Task to touch all junit xml report files for all given {@link Test} {@code tasks}.
 * This is required due to Jenkins fails if test output is created before build starts which
 * could happen using Gradles up-to-date feature :(
 */
open class TouchTestResults : DefaultTask() {
    @InputFiles
    val tasks = mutableListOf<TaskProvider<Test>>()

    fun tasks(vararg testTasks: TaskProvider<Test>) {
        tasks.addAll(testTasks)
        mustRunAfter(testTasks)
    }

    @TaskAction
    fun touch() {
        tasks.forEach { test ->
            val testResultsDir = test.get().reports.junitXml.destination
            if (testResultsDir.exists()) {
                val timestamp = System.currentTimeMillis()
                testResultsDir.listFiles()?.forEach { file ->
                    file.setLastModified(timestamp)
                }
            }
        }
    }
}

apply(from = "legacy.build.gradle")
