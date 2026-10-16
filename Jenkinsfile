pipeline {
    agent {label 'pipeline-agent'}

    stages {

        stage('Check Java') {
            steps {
                sh '''
                echo "JAVA_HOME=$JAVA_HOME"
                which java
                which mvn
                mvn -version
                '''
            }
        }
        stage('Build and test') {
            steps {
                sh '/usr/bin/java -version'
                sh 'mvn -version'
                sh 'mvn -B clean verify'
            }
            // post {
            //     always {
            //         junit 'maven-hello/target/surefire-reports/*.xml'
            //     }
            // }
        }

        stage('Save artifact') {
            steps {
                archiveArtifacts artifacts: 'maven-hello/target/*.jar', fingerprint: true
            }
        }
    }
}
