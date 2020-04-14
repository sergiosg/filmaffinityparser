package sergiosg;


import java.io.File;
import java.time.Duration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static java.time.Duration.ofMinutes;
import static java.time.Duration.ofSeconds;
import static org.awaitility.Awaitility.await;
import static org.testcontainers.containers.wait.strategy.Wait.forListeningPort;
import static org.testcontainers.containers.wait.strategy.Wait.forLogMessage;

@Testcontainers
public class AcceptanceTest {

    private static final Logger logger = LoggerFactory.getLogger(AcceptanceTest.class);
    private static final Duration DOCKER_TIMEOUT = ofMinutes(5);

    @Container
    static DockerComposeContainer container = new DockerComposeContainer(new File("docker-compose.yml"))
            .withLocalCompose(true)
            .waitingFor("kafka", forListeningPort().withStartupTimeout(DOCKER_TIMEOUT));



}
