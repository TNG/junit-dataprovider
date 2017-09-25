pipeline {
    agent any

    stages {
        stage('Preparation') {
            steps {
                git '/home/schmida/work/misc-projects/junit-dataprovider'
            }
        }
        stage('Build') {
            steps {
                sh './gradlew clean assemble'
                archiveArtifacts '**/build/libs/*.jar'
            }
        }
        stage('Unit tests') {
            steps {
                sh './gradlew test'
                junit '**/build/test-results/test/*.xml'
            }
        }
        stage('Integration tests') {
            steps {
                parallel(
                    junitIntegTest: {
                        sh './gradlew integTest'
                        junit '**/build/test-results/integTest/*.xml'
                    },
                    mavenIntegTest: {
                        sh 'cd junit4 && mvn test'
                        junit 'junit4/build/maven-target/surefire-reports/*.xml'
                    }
                )
            }
        }
        stage('Static analyses') {
            steps {
                sh './gradlew build'
                dry canComputeNew: false, defaultEncoding: '', healthy: '', pattern: 'build/reports/cpd/*.xml', unHealthy: ''
                findbugs defaultEncoding: '', excludePattern: '**/*Test.java', healthy: '', includePattern: '', pattern: '**/build/reports/findbugs/*.xml', unHealthy: '', unstableNewAll: '0'
                jacoco classPattern: '**/build/classes/main', execPattern: '**/build/jacoco/*.exec', sourcePattern: 'src/main/java'
                warnings canComputeNew: false, canResolveRelativePaths: false, categoriesPattern: '', consoleParsers: [[parserName: 'Java Compiler (Eclipse)'], [parserName: 'Java Compiler (javac)']], defaultEncoding: '', excludePattern: '', healthy: '', includePattern: '', messagesPattern: '', unHealthy: ''
            }
        }
    }
}
