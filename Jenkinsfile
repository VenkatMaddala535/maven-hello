
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
                    mvn -B test
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

        stage('SonarQube Analysis'){
            steps{
                withSonarQubeEnv('SonarQube'){
                    script{
                        def scannerHome = tool 'SonarScanner'
                        sh "${scannerHome}/bin/sonar-scanner"
                    }
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                sh '''
                    docker build \
                        -t maven-hello:${BUILD_NUMBER} \
                        -t maven-hello:latest .

                    docker images | grep maven-hello
                '''
            }
        }

        stage('Push to Docker Hub') {
            steps {
                echo 'DEBUG 1: Push stage started'

                withCredentials([
                    usernamePassword(
                        credentialsId: 'docker-push-cred',
                        usernameVariable: 'DOCKERHUB_USER',
                        passwordVariable: 'DOCKERHUB_TOKEN'
                    )
                ]) {
                    sh '''
                        set -eu

                        echo "DEBUG 2: Credentials loaded"
                        DOCKERHUB_REPO="${DOCKERHUB_USER}/maven-hello"

                        echo "DEBUG 3: Checking Docker access"
                        docker info >/dev/null

                        echo "DEBUG 4: Logging in"
                        # docker login --username <username> --password-stdin
                        echo "$DOCKERHUB_TOKEN" | docker login --username "$DOCKERHUB_USER" --password-stdin

                        echo "DEBUG 5: Tagging images"
                        # docker tag SOURCE_IMAGE[:TAG] TARGET_IMAGE[:TAG]
                        docker tag maven-hello:${BUILD_NUMBER} "$DOCKERHUB_REPO:${BUILD_NUMBER}"
                        docker tag maven-hello:latest "$DOCKERHUB_REPO:latest"

                        echo "DEBUG 6: Pushing images"
                        docker push "$DOCKERHUB_REPO:${BUILD_NUMBER}"
                        docker push "$DOCKERHUB_REPO:latest"

                        docker logout
                        echo "DEBUG 7: Push completed"
                    '''
                }
            }
        }
    }
}
