#!groovy
@Library('github.com/cloudogu/ces-build-lib@ac17d45')
import com.cloudogu.ces.cesbuildlib.*

node() { // No specific label

  properties([
    // Keep only the last 10 build to preserve space
    buildDiscarder(logRotator(numToKeepStr: '10')),
    // Don't run concurrent builds for a branch, because they use the same workspace directory
    disableConcurrentBuilds()
  ])

  String defaultEmailRecipients = env.EMAIL_SCM_RECIPIENTS

  catchError {

    Maven mvn = new MavenWrapper(this)
    // Maven build specified it must be 1.8.0-101 or newer
    def javaHome = tool 'JDK-1.8.0-101+'

    withEnv(["JAVA_HOME=${javaHome}", "PATH=${env.JAVA_HOME}/bin:${env.PATH}"]) {

      stage('Checkout') {
        checkout scm
      }

      stage('Build') {
        mvn 'clean install -DskipTests -DperformRelease -Dmaven.javadoc.failOnError=false'
      }

      stage('Unit Test') {
        mvn 'test -Dsonia.scm.test.skip.hg=true'
      }

      stage('SonarQube') {
        def sonarQube = new SonarQube(this, 'ces-sonar')

        sonarQube.analyzeWith(mvn)
      }
    }
  }

  // Archive Unit and integration test results, if any
  junit allowEmptyResults: true, testResults: '**/target/failsafe-reports/TEST-*.xml,**/target/surefire-reports/TEST-*.xml,**/target/jest-reports/TEST-*.xml'

  // Find maven warnings and visualize in job
  warnings consoleParsers: [[parserName: 'Maven']], canRunOnFailed: true

  mailIfStatusChanged(defaultEmailRecipients)
}
