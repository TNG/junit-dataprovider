import com.github.spotbugs.SpotBugsExtension
import org.w3c.dom.Element
import org.w3c.dom.NodeList
import java.time.LocalDate
import java.time.format.DateTimeFormatter

plugins {
    `java-library`
    jacoco

    id("com.github.spotbugs")
    id("biz.aQute.bnd.builder")

    `maven-publish`
    signing
}

val isBuildOnJenkins: Boolean by rootProject.extra
val skipSpotBugs: Boolean by rootProject.extra
val deps: Dependencies by rootProject.extra

repositories {
    mavenCentral()
}

group = rootProject.group
version = rootProject.version

dependencies {
    "compileOnly"(deps.spotBugsAnnotations)
    "testImplementation"(deps.spotBugsAnnotations)
}

configure<JavaPluginExtension> {
    withJavadocJar()
    withSourcesJar()
}

// configure after properties are set and integration tests are added
configure<JacocoPluginExtension> {
    toolVersion = "0.8.3"
}

configure<SpotBugsExtension> {
    toolVersion = "3.1.12"
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.compilerArgs.addAll(listOf("-Xlint:all", "-Werror"))
    }

    withType<Jar> {
        from(rootProject.rootDir) {
            include("LICENSE", "NOTICE")
            into("META-INF")
        }
    }

    named<Javadoc>("javadoc") {
        if (JavaVersion.current().isJava9Compatible) {
            (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
        }
    }

    named<Jar>("jar") {
        manifest {
            val now = LocalDate.now()

            val title = project.the<BasePluginConvention>().archivesBaseName
            val company = "TNG Technology Consulting GmbH"
            val today = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            val copyright = "${now.year} $company"

            attributes(
                    "Built-By" to "Gradle ${gradle.gradleVersion}",
                    "Built-Date" to today, // using now would destroy incremental build feature
                    "Specification-Title" to title,
                    "Specification-Version" to archiveVersion,
                    "Specification-Vendor" to company,
                    "Implementation-Title" to title,
                    "Implementation-Version" to archiveVersion,
                    "Implementation-Vendor" to company,
                    "Issue-Tracker" to "https://github.com/TNG/junit-dataprovider/issues",
                    "Documentation-URL" to "https://github.com/TNG/junit-dataprovider/wiki",
                    "Copyright" to copyright,
                    "License" to "Apache License v2.0, January 2004",

                    // OSGi / p2 plugin information
                    "Bundle-Copyright" to copyright,
                    "Bundle-Name" to title,
                    "Bundle-SymbolicName" to "${project.group}.$title",
                    "Bundle-Vendor" to company,
                    "Export-Package" to "com.tngtech.junit.dataprovider.*",

                    // Bnd plugin instructions -- remove field because it breaks caching builds with included linux timestamp
                    "-removeheaders" to "Bnd-LastModified"
            )
        }
    }

    withType<com.github.spotbugs.SpotBugsTask> {
        enabled = !skipSpotBugs
        reports {
            html.isEnabled = true
            xml.isEnabled = false
        }
    }

    val test = named<Test>("test") {
        ignoreFailures = isBuildOnJenkins
    }

    val touchTestResultsForJenkins = register<TouchTestResults>("touchTestResultsForJenkins") {
        tasks(test)
        enabled = isBuildOnJenkins
    }
    named("build") {
        dependsOn(touchTestResultsForJenkins)
    }
}

// -- sign and publish artifacts -------------------------------------------------------------------------------------
val isReleaseVersion by extra(!project.version.toString().endsWith("-SNAPSHOT"))

// username and password from gradle.properties otherwise empty
val sonatypeUsername by extra(findProperty("sonatypeUsername")?.toString() ?: "")
val sonatypePassword by extra(findProperty("sonatypePassword")?.toString() ?: "")

tasks.withType<GenerateModuleMetadata> {
    enabled = isReleaseVersion // signing of these artifacts causes failure for snapshot versions
}

configure<PublishingExtension> {
    publications {
        register<MavenPublication>("mavenJava") {
            val archivesBaseName = project.the<BasePluginConvention>().archivesBaseName
            artifactId = archivesBaseName
            from(components["java"])
            pom {
                packaging = "jar"

                name.set(archivesBaseName)
                description.set(project.description)
                url.set("https://github.com/TNG/junit-dataprovider")

                developers {
                    developer {
                        id.set("aaschmid")
                        name.set("Andreas Schmid")
                        email.set("service@aaschmid.de")
                    }
                }

                licenses {
                    license {
                        name.set("The Apache Software License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        distribution.set("repo")
                    }
                }

                scm {
                    connection.set("scm:git@github.com:TNG/junit-dataprovider.git")
                    developerConnection.set("scm:git@github.com:TNG/junit-dataprovider.git")
                    url.set("scm:git@github.com:TNG/junit-dataprovider.git")
                }

                withXml {
                    fun NodeList.asElementList() = (0 until length).map { this::item }.filterIsInstance<Element>()
                    fun NodeList.onlyElement() = if (length == 1) item(0) else throw IllegalStateException("Expected only one element but got $length.")

                    asElement()
                            .getElementsByTagName("dependencies")
                            .asElementList()
                            .flatMap { it.childNodes.asElementList() }
                            .forEach { dep ->
                                val groupId = dep.getElementsByTagName("groupId").onlyElement()
                                val artifactId = dep.getElementsByTagName("artifactId").onlyElement()

                                // JUnit4
                                if (groupId.textContent == "junit" && artifactId.textContent == "junit") {
                                    dep.getElementsByTagName("version").onlyElement().textContent = "[4.10,4.12]"
                                    dep.getElementsByTagName("scope").onlyElement().textContent = "provided"
                                }

                                // JUnit5
                                if ((groupId.textContent == "org.junit.jupiter" && artifactId.textContent == "junit-jupiter-engine") ||
                                        (groupId.textContent == "org.junit.jupiter" && artifactId.textContent == "junit-jupiter-params")) {
                                    dep.getElementsByTagName("version").onlyElement().textContent = "[5.5.0-M6,6.0.0)"
                                    dep.getElementsByTagName("scope").onlyElement().textContent = "provided"
                                }
                            }
                }
            }
            // finally call `asNode` to get rid of excessive newlines caused by use of asElement
            // see also https://github.com/gradle/gradle/issues/7529
            afterEvaluate {
                pom {
                    withXml {
                        asNode()
                    }
                }
            }
        }
    }

    repositories {
        maven {
            val releasesRepoUrl = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
            val snapshotRepoUrl = uri("https://oss.sonatype.org/content/repositories/snapshots/")
            url = if (isReleaseVersion) releasesRepoUrl else snapshotRepoUrl

            credentials {
                username = sonatypeUsername
                password = sonatypePassword
            }

            metadataSources {
                gradleMetadata()
            }
        }
    }
}

// requires gradle.properties, see http://www.gradle.org/docs/current/userguide/signing_plugin.html
configure<SigningExtension> {
    setRequired({ isReleaseVersion && gradle.taskGraph.hasTask("publish") })
    sign(the<PublishingExtension>().publications["mavenJava"])
}
