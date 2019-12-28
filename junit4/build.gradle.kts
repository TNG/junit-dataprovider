plugins {
    `dataprovider-library`
    `integration-test`
}

val deps: Dependencies by rootProject.extra

base {
    archivesBaseName = "junit4-dataprovider"
    description = "A TestNG like dataprovider runner for JUnit having a simplified syntax compared to all the existing JUnit4 features."
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_6
    targetCompatibility = JavaVersion.VERSION_1_6
}

dependencies {
    "api"(project(":core"))
    "api"(deps.junit4)

    "testImplementation"(deps.assertJ6)
    "testImplementation"(deps.mockito6)
}

tasks {
    withType<JavaCompile> {
        options.compilerArgs.addAll(listOf("-Xlint:-options"))
    }

    jar {
        manifest {
            attributes(
                    "Automatic-Module-Name" to "com.tngtech.junit.dataprovider.junit4"
            )
        }
    }
}
