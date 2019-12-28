plugins {
    `dataprovider-library`
}

val deps: Dependencies by rootProject.extra

base {
    archivesBaseName = "junit-dataprovider-core"
    description = "The common core for a TestNG like dataprovider runner for JUnit."
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_6
    targetCompatibility = JavaVersion.VERSION_1_6
}

dependencies {
    "testImplementation"(deps.junit4)

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
                    "Automatic-Module-Name" to "com.tngtech.junit.dataprovider.core"
            )
        }
    }
}
