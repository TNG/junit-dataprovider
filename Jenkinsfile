pipeline {
    agent any

    stages {
        stage('Preparation') {
            steps {
                checkout scm
            }
        }
        stage('Assemble') {
            steps {
                sh './gradlew clean assemble'
            }
            post {
                always {
                    archiveArtifacts '**/build/libs/*.jar'
                }
            }
        }
        stage('Unit tests') {
            steps {
                sh './gradlew test'
            }
            post {
                always {
                    junit '**/build/test-results/test/*.xml'
                }
            }
        }
        stage('Parallel integration tests') {
            parallel {
                // TODO JUnit Jupiter integration tests should be integrated here as soon as
                // "junit-platform-gradle-plugin" is able to build separate sourceSets
                stage('Integration tests') {
                    steps {
                        sh './gradlew integTest'
                    }
                    post {
                        always {
                                junit '**/build/test-results/integTest/*.xml'
                        }
                    }
                }
                stage('JUnit4 Maven integration test') {
                    steps {
                        sh 'cd ${WORKSPACE}/junit4 && mvn test'
                    }
                    post {
                        always {
                            sh 'cd ${WORKSPACE}'
                            junit 'junit4/build/maven-target/surefire-reports/*.xml'
                        }
                    }
                }
                stage('JUnit Jupiter Maven integration test') {
                    steps {
                        sh 'cd ${WORKSPACE}/junit-jupiter && mvn test'
                    }
                    post {
                        always {
                            sh 'cd ${WORKSPACE}'
                            junit 'junit-jupiter/build/maven-target/surefire-reports/*.xml'
                        }
                    }
                }
                stage('JUnit Jupiter Parameterized Maven integration test') {
                    steps {
                        sh 'cd junit-jupiter-params && mvn test'
                    }
                    post {
                        always {
                            sh 'cd ${WORKSPACE}'
                            junit 'junit-jupiter-params/build/maven-target/surefire-reports/*.xml'
                        }
                    }
                }
            }
        }
        stage('Static code analyses') {
            steps {
                sh './gradlew build'
            }
            post {
                always {
                    dry canComputeNew: false, defaultEncoding: '', healthy: '', pattern: 'build/reports/cpd/*.xml', unHealthy: ''
                    findbugs defaultEncoding: '', excludePattern: '**/*Test.java', healthy: '', includePattern: '', pattern: '**/build/reports/findbugs/*.xml', unHealthy: '', unstableNewAll: '0'
                    jacoco classPattern: '**/build/classes/*/main/', execPattern: '**/build/jacoco/*.exec', sourcePattern: '**/src/main/java'
                    warnings canComputeNew: false, canResolveRelativePaths: false, categoriesPattern: '', consoleParsers: [[parserName: 'Java Compiler (Eclipse)'], [parserName: 'Java Compiler (javac)'], [parserName: 'JavaDoc Tool']], defaultEncoding: 'UTF-8', excludePattern: '', healthy: '', includePattern: '', messagesPattern: '', unHealthy: ''
                }
            }
        }
    }
}
