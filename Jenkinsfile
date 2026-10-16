
pipeline {
    agent { label 'pipeline-agent' }

    stages {
        stage('Build and Test') {
            steps {
                sh '''
                    export JAVA_HOME=/usr/lib/jvm/java-21
                    export PATH="$JAVA_HOME/bin:$PATH"

                    java -version
                    mvn -version
                    mvn -B clean verify
                '''
            }
            post {
                always {
                    junit allowEmptyResults: true,
                          testResults: 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Save artifact') {
            steps {
                archiveArtifacts artifacts: 'target/*.jar',
                                 fingerprint: true
            }
        }
    }
}
