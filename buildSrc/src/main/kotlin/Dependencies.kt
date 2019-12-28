class Dependencies(private val junit4Version: String, private val junitJupiterVersion: String) {
    init {
        println("Using JUnit4 version $junit4Version for current build.")
        println("Using JUnit Jupiter version $junitJupiterVersion for current build.")
    }

    val spotBugsAnnotations = "com.github.spotbugs:spotbugs-annotations:3.1.12"

    val junit4 = "junit:junit:$junit4Version"
    val junitJupiterEngine = "org.junit.jupiter:junit-jupiter-engine:$junitJupiterVersion"
    val junitJupiterParams = "org.junit.jupiter:junit-jupiter-params:$junitJupiterVersion"

    val assertJ6 = "org.assertj:assertj-core:1.6.0"
    val mockito6 = "org.mockito:mockito-core:2.28.2"
    val assertJ8 = "org.assertj:assertj-core:3.14.0"
    val mockito8 = "org.mockito:mockito-core:3.2.4"

    val groovy = "org.codehaus.groovy:groovy:2.5.8"
}
