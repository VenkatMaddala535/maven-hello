pipeline {
    agent {label 'pipeline-agent'}

    stages {
        stage('Build and test') {
            steps {
                sh 'mvn -version'
                sh 'mvn -B clean verify'
            }
            post {
                always {
                    junit 'maven-hello/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Save artifact') {
            steps {
                archiveArtifacts artifacts: 'maven-hello/target/*.jar', fingerprint: true
            }
        }
    }
}
