# Maven Hello

A Java 21 website using the JDK HTTP server. No application framework or database is needed.

Run these commands **inside `maven-hello/`**:

```bash
mvn -B clean verify
java -jar target/maven-hello-1.0.0.jar
```

Open http://localhost:8081. Stop with **Ctrl+C**.

| Task | Command / output |
| --- | --- |
| Test only | `mvn test` |
| Test + coverage + package | `mvn -B clean verify` |
| Website | `/` |
| Greeting | `/api/greet?name=Balaji` → `Hello, Balaji!` |
| Health | `/health` → `{"status":"ok","stack":"maven"}` |
| Package | `target/maven-hello-1.0.0.jar` |
| Test reports | `target/surefire-reports/*.xml` |
| Coverage | `target/site/jacoco/index.html` and `jacoco.xml` |

Use another port: `PORT=8082 java -jar target/maven-hello-1.0.0.jar`.

`mvn verify` runs the Maven lifecycle: compile → test → package → verify. JaCoCo writes coverage during `verify`. The JAR includes HTML, CSS, and JavaScript from `src/main/resources/public/`.

The Jenkinsfile runs `mvn -B clean verify`, publishes JUnit results, and archives the JAR. The initial `1.0.0` version is fixed for learning; Jenkins keeps each build's artifact separately.

For a Docker build agent, select **`maven-hello/Jenkinsfile.docker`** as the Jenkins Script Path. It uses `maven:3.9.11-eclipse-temurin-21` on a node labeled `docker`. See the [Docker agent runbook](../runbooks/05-docker-agents.md).

[Local runbook](../runbooks/01-local-projects.md) · [Jenkins runbook](../runbooks/02-jenkins-ci.md) · [Optional integrations](../runbooks/03-sonarqube-jfrog.md)
